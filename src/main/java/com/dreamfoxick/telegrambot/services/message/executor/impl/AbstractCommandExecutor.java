package com.dreamfoxick.telegrambot.services.message.executor.impl;

import com.dreamfoxick.telegrambot.services.message.executor.CommandExecutor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator.createSendMessageWithReplyKeyboard;

public abstract class AbstractCommandExecutor implements CommandExecutor {

    @Override
    public void execute(long chatId,
                        TelegramLongPollingBot bot) throws TelegramApiException {
        this.updateState(chatId);
        bot.execute(createSendMessageWithReplyKeyboard(
                chatId,
                this.getResponseText(),
                this.getResponseKeyboard()
        ));
    }

    protected abstract void updateState(long chatId);

    protected abstract String getResponseText();

    protected abstract ReplyKeyboardMarkup getResponseKeyboard();
}
