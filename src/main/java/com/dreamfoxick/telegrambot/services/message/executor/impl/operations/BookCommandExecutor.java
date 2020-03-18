package com.dreamfoxick.telegrambot.services.message.executor.impl.operations;

import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.message.executor.impl.AbstractCommandExecutor;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.dreamfoxick.telegrambot.services.message.executor.CommandResponseText.BOOK;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_BOOKS_STATE;

@Component
public class BookCommandExecutor extends AbstractCommandExecutor {
    private final StateController<Long, State> controller;

    public BookCommandExecutor(@Qualifier("stateController") StateController<Long, State> controller) {
        this.controller = controller;
    }

    @Override
    protected void updateState(long chatId) {
        controller.updateIfPresent(chatId, FIND_BOOKS_STATE);
    }

    @Override
    protected String getResponseText() {
        return BOOK.getResponseText();
    }

    @Override
    protected ReplyKeyboardMarkup getResponseKeyboard() {
        return KeyboardCreator.backKeyboard();
    }
}
