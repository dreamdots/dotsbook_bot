package com.dreamfoxick.telegrambot.services.message.handler.exception;

import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

import static com.dreamfoxick.telegrambot.services.statecontroller.State.MAIN_MENU_STATE;

@Component
@PropertySource("classpath:bot.properties")
public class ExceptionProcessor {
    private final StateController<Long, State> stateController;

    @Value("${admin_chat_id}")
    private long adminChatId;

    public ExceptionProcessor(StateController<Long, State> stateController) {
        this.stateController = stateController;
    }

    @SneakyThrows
    public void processIllegalArgumentException(long chatId,
                                                IllegalArgumentException ex,
                                                TelegramLongPollingBot bot) {
        ex.printStackTrace();
        this.process(chatId, ex.getMessage(), bot);
    }

    @SneakyThrows
    public void processAnyException(long chatId,
                                    Exception ex,
                                    TelegramLongPollingBot bot) {
        stateController.updateIfPresent(chatId, MAIN_MENU_STATE);
        ex.printStackTrace();
        this.process(chatId, "Произошла ошибка, попробуйте позже", bot);
        this.process(adminChatId, Arrays.toString(ex.getStackTrace()), bot);
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
