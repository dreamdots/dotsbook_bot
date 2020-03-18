package com.dreamfoxick.telegrambot.services.message.processor;

import com.dreamfoxick.telegrambot.utils.annotation.ReadyToUse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@ReadyToUse
public interface CommandProcessor {

    void process(long chatId,
                 String link,
                 TelegramLongPollingBot bot) throws TelegramApiException, IOException;
}
