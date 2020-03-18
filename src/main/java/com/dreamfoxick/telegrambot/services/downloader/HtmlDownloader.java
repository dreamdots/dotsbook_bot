package com.dreamfoxick.telegrambot.services.downloader;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public interface HtmlDownloader {

    /**
     * Usage apache http client for download html code
     */
    String downloadHTML(String URL) throws IOException;

    File downloadFile(long chatId,
                      String URL,
                      TelegramLongPollingBot bot) throws IOException, TelegramApiException;
}
