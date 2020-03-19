package com.dreamfoxick.telegrambot.services.downloader.file;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public interface FileDownloader {

    File download(long chatId,
                  String fileId,
                  TelegramLongPollingBot bot) throws IOException, TelegramApiException;

}
