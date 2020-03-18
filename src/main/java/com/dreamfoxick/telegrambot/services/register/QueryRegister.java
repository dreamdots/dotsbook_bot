package com.dreamfoxick.telegrambot.services.register;

import com.dreamfoxick.telegrambot.utils.annotation.ReadyToUse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@ReadyToUse
public interface QueryRegister {

    void registerUpdateQueries(long chatId,
                               List<String> response,
                               TelegramLongPollingBot bot) throws TelegramApiException;

    void registerDownloadQueries(long chatId,
                                 String bookId,
                                 List<String> response,
                                 TelegramLongPollingBot bot) throws TelegramApiException;
}
