package com.dreamfoxick.telegrambot.services.message.executor.impl.main;

import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.message.executor.impl.AbstractCommandExecutor;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.dreamfoxick.telegrambot.services.message.executor.CommandResponseText.DEFAULT;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.MAIN_MENU_STATE;

@Component
public class DefaultCommandExecutor extends AbstractCommandExecutor {
    private final StateController<Long, State> stateController;

    public DefaultCommandExecutor(@Qualifier("stateController") StateController<Long, State> stateController) {
        this.stateController = stateController;
    }

    @Override
    protected void updateState(long chatId) {
        stateController.updateIfPresent(chatId, MAIN_MENU_STATE);
    }

    @Override
    protected String getResponseText() {
        return DEFAULT.getResponseText();
    }

    @Override
    protected ReplyKeyboardMarkup getResponseKeyboard() {
        return KeyboardCreator.mainKeyboard();
    }
}
