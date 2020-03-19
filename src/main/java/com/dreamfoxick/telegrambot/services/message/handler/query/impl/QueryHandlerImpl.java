package com.dreamfoxick.telegrambot.services.message.handler.query.impl;

import com.dreamfoxick.telegrambot.services.downloader.file.FileDownloader;
import com.dreamfoxick.telegrambot.services.enums.Pattern;
import com.dreamfoxick.telegrambot.services.message.handler.exception.ExceptionProcessor;
import com.dreamfoxick.telegrambot.services.message.handler.query.QueryHandler;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.CallbackData.BACK;
import static com.dreamfoxick.telegrambot.services.enums.CallbackData.NEXT;
import static com.dreamfoxick.telegrambot.services.enums.RegEx.DOWNLOAD_QUERY;
import static com.dreamfoxick.telegrambot.services.enums.RegEx.UPDATE_QUERY;
import static com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator.backKeyboard;
import static com.dreamfoxick.telegrambot.services.message.creator.SendDocumentCreator.createSendDocumentWithReplyKeyboard;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class QueryHandlerImpl implements QueryHandler {
    private final StateController<String, Map<Integer, EditMessageText>> updateQueryController;
    private final StateController<String, String> downloadQueryController;

    private final ExceptionProcessor exceptionProcessor;
    private final FileDownloader fileDownloader;

    @Override
    public void processQuery(CallbackQuery query,
                             TelegramLongPollingBot bot) {
        val message = query.getMessage();
        val chatId = message.getChatId();
        val queryData = query.getData();
        try {
            if (queryData.matches(DOWNLOAD_QUERY.getRegEx())) this.processDownloadQuery(chatId, queryData, bot);
            else if (queryData.matches(UPDATE_QUERY.getRegEx())) this.processUpdateQuery(query, bot);
            else {
                log.warn(format("Invalid query request: %s", queryData));
                throw new IllegalArgumentException("Ошибка обработки кнопки. Попробуйте заного найти книгу/автора");
            }
        } catch (IllegalArgumentException exc) {
            exceptionProcessor.processIllegalArgumentException(chatId, exc, bot);
        } catch (Exception e) {
            exceptionProcessor.processAnyException(chatId, e, bot);
        }
    }

    private void processDownloadQuery(long chatId,
                                      String queryData,
                                      TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        log.info(format("Received download query -> %s", queryData));
        val URL = downloadQueryController.get(queryData);
        val bookFile = fileDownloader.download(chatId, URL, bot);
        val bookDocument = createSendDocumentWithReplyKeyboard(chatId, bookFile, backKeyboard());
        bot.execute(bookDocument);
        Files.delete(bookFile.toPath());
    }

    private void processUpdateQuery(CallbackQuery query,
                                    TelegramLongPollingBot bot) throws TelegramApiException {
        val message = query.getMessage();
        val chatId = message.getChatId();
        val replyId = message.getMessageId();
        val updateKey = format(Pattern.UPDATE_QUERY_CALLBACK_DATA.getPattern(), chatId, replyId);
        log.info(format("Received update query -> %s", updateKey));
        val editMessages = updateQueryController.get(format(Pattern.UPDATE_QUERY_CALLBACK_DATA.getPattern(), chatId, replyId));
        val queryData = query.getData();
        if (queryData.contains(NEXT.getCallback())) {
            val index = Integer.parseInt(queryData.replaceAll(NEXT.getCallback(), ""));
            bot.execute(editMessages.get(index + 1));
        } else {
            val index = Integer.parseInt(queryData.replaceAll(BACK.getCallback(), ""));
            bot.execute(editMessages.get(index - 1));
        }
    }
}
