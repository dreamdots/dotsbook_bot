package com.dreamfoxick.telegrambot.services.message.handler.query;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface QueryHandler {

    void processQuery(CallbackQuery query,
                      TelegramLongPollingBot bot);
}
