package com.dreamfoxick.telegrambot.services.downloader.impl;

import com.dreamfoxick.telegrambot.services.downloader.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.message.creator.EditMessageCreator;
import com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

@Slf4j
@Component
public class HtmlDownloaderImpl implements HtmlDownloader {
    private final static Path dir = initializeDirectory();
    private final HttpClient client = HttpClients.createMinimal();

    @SneakyThrows
    private static Path initializeDirectory() {
//        val startUpURL = App.getStartUpLocation();
//        val resultPath = Paths.get(startUpURL.toString()
//                .replaceAll("telegrambot-0.0.1.jar!/BOOT-INF/classes!/", "tempfiles")
//                .replaceAll("jar:file:", ""));
//        if (Files.notExists(resultPath)) {
//            Files.createDirectories(resultPath);
//            log.info(format("Created directory: %s", resultPath.toString()));
//        }
//        return resultPath;
        return Paths.get("./tempfiles");
    }

    @Override
    public String downloadHTML(String URL) throws IOException {
        val getRequest = new HttpGet(URL);
        log.info(format("Created http get request for URL: %s", URL));
        val response = client.execute(getRequest);
        log.info("Request successfully executed");
        val responseEntity = response.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    @Override
    public File downloadFile(long chatId,
                             String URL,
                             TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        try {
            log.info(format("Try to download file from URL: %s", URL));
            // создаю временный zip архив
            val pathToArchive = this.createZipArchive(chatId);

            // отправляю первое сообщение
            int replyId = this.sendHeadingMessage(chatId, bot);
            // отправляю Head запрос и узнаю размер книги
            long bookSize = this.getBookSize(URL);
            log.info(format("File size: %d", bookSize));

            try (val bis = this.getInputStreamOfURL(URL);
                 val fos = this.getOutputStreamOfTempArchive(pathToArchive)) {
                this.sendStateMessage(chatId, replyId, "_Начинаю загрузку книги_", bot);
                log.info("Download started");

                if (bookSize == -1) this.downloadFile(bis, fos);
                else this.downloadFileWithUpdateMessage(chatId, replyId, bot, bookSize, bis, fos);

                log.info("Download successfully executed");
                this.sendStateMessage(chatId, replyId, "*↓ Книга успешно загружена ↓*", bot);
            }

            return pathToArchive.toFile();
        } catch (IOException ex) {
            throw new IOException("Ошибка установки соединения с библиотекой. Попробуйте позже");
        }
    }

    private void downloadFile(BufferedInputStream bis,
                              FileOutputStream fos) throws IOException {
        bis.transferTo(fos);
    }

    private void downloadFileWithUpdateMessage(long chatId,
                                               int replyId,
                                               TelegramLongPollingBot bot,
                                               long bookSize,
                                               BufferedInputStream bis,
                                               FileOutputStream fos) throws IOException, TelegramApiException {
        byte[] buffer;
        for (int i = 0; i <= 10; i++) {
            buffer = bis.readNBytes((int) (bookSize / 10));
            fos.write(buffer);
            this.sendStateMessage(chatId, replyId, format("_Загружено: %d%%_", i * 10), bot);
            log.info(format("File uploaded: %d%%", i * 10));
        }
    }

    private FileOutputStream getOutputStreamOfTempArchive(Path pathToArchive) throws FileNotFoundException {
        return new FileOutputStream(pathToArchive.toFile());
    }

    private BufferedInputStream getInputStreamOfURL(String URL) throws IOException {
        val getConnection = (HttpURLConnection) new URL(URL).openConnection();
        return new BufferedInputStream(getConnection.getInputStream());
    }

    private Path createZipArchive(long chatId) throws IOException {
        return Files.createTempFile(dir, String.valueOf(chatId), ".zip");
    }

    private int sendHeadingMessage(long chatId,
                                   TelegramLongPollingBot bot) throws TelegramApiException {
        return bot.execute(SendMessageCreator.createSendMessageWithoutKeyboard(chatId,
                "_Установка соединения с библиотекой_")).getMessageId();
    }

    private void sendStateMessage(long chatId,
                                  int replyId,
                                  String stateText,
                                  TelegramLongPollingBot bot) throws TelegramApiException {
        bot.execute(EditMessageCreator.withoutKeyboard(chatId, replyId, stateText));
    }

    private long getBookSize(String URL) throws IOException {
        val headConnection = (HttpURLConnection) new URL(URL).openConnection();
        headConnection.setRequestMethod("HEAD");
        return headConnection.getContentLengthLong();
    }
}
