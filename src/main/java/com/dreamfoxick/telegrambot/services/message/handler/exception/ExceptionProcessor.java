package com.dreamfoxick.telegrambot.services.message.handler.exception;

import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import com.dreamfoxick.telegrambot.utils.LoggerUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.dreamfoxick.telegrambot.services.statecontroller.State.MAIN_MENU_STATE;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@PropertySource("classpath:bot.properties")
public class ExceptionProcessor {
    private final StateController<Long, State> stateController;

    @SneakyThrows
    public void processIllegalArgumentException(long chatId,
                                                IllegalArgumentException ex,
                                                TelegramLongPollingBot bot) {
        LoggerUtils.logStackTrace(ex);
        this.process(chatId, ex.getMessage(), bot);
    }

    @SneakyThrows
    public void processJsonMappingException(long chatId,
        JsonMappingException ex,
        TelegramLongPollingBot bot) {
        LoggerUtils.logStackTrace(ex);
    }

    @SneakyThrows
    public void processAnyException(long chatId,
                                    Exception ex,
                                    TelegramLongPollingBot bot) {
        stateController.updateIfPresent(chatId, MAIN_MENU_STATE);
        LoggerUtils.logStackTrace(ex);
        this.process(chatId, ex.getMessage(), bot);
    }

    private void process(long chatId,
                         String exceptionMessage,
                         TelegramLongPollingBot bot) throws TelegramApiException {
        bot.execute(SendMessageCreator.createSendMessageWithReplyKeyboard(
                chatId,
                exceptionMessage,
                KeyboardCreator.mainKeyboard()
        ));
    }
}
