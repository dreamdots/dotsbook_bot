package com.dreamfoxick.telegrambot.bot;

import com.dreamfoxick.telegrambot.services.message.handler.update.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@PropertySource("classpath:bot.properties")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BookLongPollBot extends TelegramLongPollingBot {

    private final UpdateHandler handler;


    @Value("${bot_name}")
    private String botName;
    @Value("${bot_token}")
    private String botToken;


    @Override
    public void onUpdateReceived(Update update) {
        handler.processUpdate(update, this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
