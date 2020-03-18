package com.dreamfoxick.telegrambot.services.message.handler.update;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    void processUpdate(Update update,
                       TelegramLongPollingBot bot);

}
