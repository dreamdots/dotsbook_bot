package com.dreamfoxick.telegrambot.services.message.handler.update.impl;

import com.dreamfoxick.telegrambot.services.message.executor.CommandExecutorInvoker;
import com.dreamfoxick.telegrambot.services.message.handler.exception.ExceptionProcessor;
import com.dreamfoxick.telegrambot.services.message.handler.query.QueryHandler;
import com.dreamfoxick.telegrambot.services.message.handler.update.UpdateHandler;
import com.dreamfoxick.telegrambot.services.message.processor.CommandProcessorInvoker;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

import static com.dreamfoxick.telegrambot.services.enums.RegEx.AUTHOR_LINK_REQUEST;
import static com.dreamfoxick.telegrambot.services.enums.RegEx.BOOK_LINK_REQUEST;
import static com.dreamfoxick.telegrambot.services.message.handler.CommandButtonText.*;
import static com.dreamfoxick.telegrambot.services.message.handler.HandlerUtil.*;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_AUTHOR_STATE;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_BOOK_STATE;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UpdateHandlerImpl implements UpdateHandler {
    private final CommandProcessorInvoker commandProcessorInvoker;
    private final CommandExecutorInvoker commandExecutorInvoker;

    private final ExceptionProcessor exceptionProcessor;
    private final StateController<Long, State> stateController;

    private final QueryHandler queryHandler;

    @Override
    @Async("longPollExec")
    public void processUpdate(Update update,
                              TelegramLongPollingBot bot) {
        if (isCallbackQueryUpdate(update)) {
            queryHandler.processQuery(update.getCallbackQuery(), bot);
        } else if (updateHasMessage(update)) {
            processUpdate(update.getMessage(), bot);
        }
    }

    private void processUpdate(Message message,
                               TelegramLongPollingBot bot) {
        val chatId = message.getChatId();
        val text = message.getText();
        log.info(format("Received message -> {%d: %s}", chatId, text));
        try {
            if (isTextMessage(message)) processMessage(chatId, text, bot);
            else {
                log.warn("Message dont have any text params");
                throw new IllegalArgumentException("Я не умею обрабатывать такие сообщения");
            }
        } catch (IllegalArgumentException exc) {
            exceptionProcessor.processIllegalArgumentException(chatId, exc, bot);
        } catch (Exception e) {
            exceptionProcessor.processAnyException(chatId, e, bot);
        }
    }

    private void processMessage(long chatId,
                                String command,
                                TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        if (command.equals(START.getButtonText())) {
            log.info(format("Execute '/start' command for chatId: %d", chatId));
            commandExecutorInvoker.startCommand(chatId, bot);
        } else if (command.equals(BOOK.getButtonText())) {
            log.info(format("Execute '/book' command for chatId: %d", chatId));
            commandExecutorInvoker.bookCommand(chatId, bot);
        } else if (command.equals(AUTHOR.getButtonText())) {
            log.info(format("Execute '/author' command for chatId: %d", chatId));
            commandExecutorInvoker.authorCommand(chatId, bot);
        } else if (command.equals(BACK.getButtonText())) {
            log.info(format("Execute '/back' command for chatId: %d", chatId));
            commandExecutorInvoker.backCommand(chatId, bot);
        } else {
            this.processTextMessage(chatId, command, bot);
        }
    }

    private void processTextMessage(long chatId,
                                    String text,
                                    TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        var uState = stateController.get(chatId);
        if (uState == null) {
            log.info(format("ChatId: %d dont have any state. Return to main menu", chatId));
            commandExecutorInvoker.startCommand(chatId, bot);
        } else {
            uState = this.updateUserStateIfNeeded(chatId, text, uState);
            log.info(format("ChatId: %d has a state: %s", chatId, uState));
            switch (uState) {
                case FIND_BOOK_STATE:
                    log.info(format("Execute 'findBook' command for chatId: %d", chatId));
                    commandProcessorInvoker.processFindBook(chatId, text, bot);
                    break;
                case FIND_AUTHOR_STATE:
                    log.info(format("Execute 'findAuthor' command for chatId: %d", chatId));
                    commandProcessorInvoker.processFindAuthor(chatId, text, bot);
                    break;
                case FIND_BOOKS_STATE:
                    log.info(format("Execute 'findBooks' command for chatId: %d", chatId));
                    commandProcessorInvoker.processFindBooks(chatId, text, bot);
                    break;
                case FIND_AUTHORS_STATE:
                    log.info(format("Execute 'findAuthors' command for chatId: %d", chatId));
                    commandProcessorInvoker.processFindAuthors(chatId, text, bot);
                    break;
                default:
                    commandExecutorInvoker.defaultCommand(chatId, bot);
                    break;
            }
        }
    }

    private State updateUserStateIfNeeded(long chatId,
                                          String text,
                                          State uState) {
        if (text.matches(BOOK_LINK_REQUEST.getRegEx())) {
            stateController.updateIfPresent(chatId, FIND_BOOK_STATE);
            uState = stateController.get(chatId);
        } else if (text.matches(AUTHOR_LINK_REQUEST.getRegEx())) {
            stateController.updateIfPresent(chatId, FIND_AUTHOR_STATE);
            uState = stateController.get(chatId);
        }
        return uState;
    }
}
