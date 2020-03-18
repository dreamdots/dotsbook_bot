package com.dreamfoxick.telegrambot.services.message.executor.impl.operations;

import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.message.executor.impl.AbstractCommandExecutor;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.dreamfoxick.telegrambot.services.message.executor.CommandResponseText.AUTHOR;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_AUTHORS_STATE;

@Component
public class AuthorCommandExecutor extends AbstractCommandExecutor {
    private final StateController<Long, State> controller;

    @Autowired
    public AuthorCommandExecutor(@Qualifier("stateController") StateController<Long, State> controller) {
        this.controller = controller;
    }

    @Override
    protected void updateState(long chatId) {
        controller.updateIfPresent(chatId, FIND_AUTHORS_STATE);
    }

    @Override
    protected String getResponseText() {
        return AUTHOR.getResponseText();
    }

    @Override
    protected ReplyKeyboardMarkup getResponseKeyboard() {
        return KeyboardCreator.backKeyboard();
    }
}
