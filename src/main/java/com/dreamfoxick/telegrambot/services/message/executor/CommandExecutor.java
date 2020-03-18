package com.dreamfoxick.telegrambot.services.message.executor;

import com.dreamfoxick.telegrambot.utils.annotation.ReadyToUse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ReadyToUse
public interface CommandExecutor {

    void execute(long chatId,
                 TelegramLongPollingBot bot) throws TelegramApiException;
}
