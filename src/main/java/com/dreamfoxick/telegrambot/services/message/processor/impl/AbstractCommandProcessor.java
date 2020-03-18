package com.dreamfoxick.telegrambot.services.message.processor.impl;

import com.dreamfoxick.telegrambot.services.message.processor.CommandProcessor;
import com.dreamfoxick.telegrambot.services.register.QueryRegister;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public abstract class AbstractCommandProcessor implements CommandProcessor {
    private final QueryRegister queryRegister;

    protected void sendResult(long chatId,
                              List<String> response,
                              TelegramLongPollingBot bot) throws TelegramApiException {
        queryRegister.registerUpdateQueries(chatId, response, bot);
    }

    protected abstract Map<String, String[]> getResult(String link) throws IOException;

    protected abstract void updateState(long chatId);

}
