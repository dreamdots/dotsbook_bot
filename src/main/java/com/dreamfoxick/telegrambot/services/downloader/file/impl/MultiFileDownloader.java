package com.dreamfoxick.telegrambot.services.downloader.file.impl;

import com.dreamfoxick.telegrambot.services.downloader.file.FileDownloader;
import com.dreamfoxick.telegrambot.services.message.creator.EditMessageCreator;
import com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator;
import com.dreamfoxick.telegrambot.utils.LoggerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.ONION_URL;
import static com.dreamfoxick.telegrambot.services.enums.URLConstant.SITE_URL;
import static com.dreamfoxick.telegrambot.services.message.creator.EditMessageCreator.withoutKeyboard;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bot.properties")
public class MultiFileDownloader implements FileDownloader {

    @Value("${onion_host}")
    private String HOST;
    @Value("${onion_port}")
    private int PORT;

    @Value("${temp_files_directory}")
    private Path TEMP_DIRECTORY;

    @PostConstruct
    private void initializeSystemProperty() {
//        System.setProperty("java.net.preferIPv4Stack", "true");
//        System.setProperty("socksProxyHost", HOST);
//        System.setProperty("socksProxyPort", String.valueOf(PORT));
    }

    @Override
    public File download(long chatId,
                         String fileId,
                         TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        val pathToArchive = this.createZipArchive(chatId);
        val replyId = this.sendHeadingMessage(chatId, bot);

        // узнаю размер книги
        val fileSize = this.getContentSize(chatId, replyId, fileId, bot);

        try (val bis = this.initializeInputStream(chatId, replyId, fileId, bot);
             val fos = this.initializeOutputStream(pathToArchive)) {
            if (fileSize == -1) {
                bot.execute(EditMessageCreator.withoutKeyboard(chatId, replyId,
                        "_У сервера нет данных о размере книги_"));
                this.download(chatId, replyId, bis, fos, bot);
            } else this.downloadWithTrace(chatId, replyId, fileSize, bis, fos, bot);
        }

        return pathToArchive.toFile();
    }

    /**
     * @throws IOException ошибка создания zip архива
     */
    private Path createZipArchive(long chatId) throws IOException {
        try {
            return Files.createTempFile(TEMP_DIRECTORY, String.valueOf(chatId), ".zip");
        } catch (IOException ex) {
            throw new IOException("Не могу создать архив с книгой. Попробуйте позже", ex);
        }
    }

    /**
     * @throws TelegramApiException ошибка отправки сообщения
     */
    private int sendHeadingMessage(long chatId,
                                   TelegramLongPollingBot bot) throws TelegramApiException {
        return bot.execute(SendMessageCreator.createSendMessageWithoutKeyboard(chatId,
                "_Установка соединения с библиотекой_")).getMessageId();
    }

    /**
     * Если нет доступа к сети tor, то пробую через зеркало.
     * Если и к зеркалу нет доступа, то возвращаю -1 -> размер файла неизвестен
     */
    private long getContentSize(long chatId,
                                int replyId,
                                String fileId,
                                TelegramLongPollingBot bot) throws TelegramApiException {
        try {
            bot.execute(withoutKeyboard(chatId, replyId,
                    "_Получаю размер книги_"));
            return this.openConnection(fileId, "HEAD", this.createProxy()).getContentLengthLong();
        } catch (IOException e) {
            try {
                LoggerUtils.logStackTrace(e);
                bot.execute(withoutKeyboard(chatId, replyId,
                        "_Не могу установить соединение через TOR. Пробую через зеркало_"));
                return this.openConnection(fileId, "HEAD", null).getContentLengthLong();
            } catch (IOException ex) {
                LoggerUtils.logStackTrace(ex);
                bot.execute(EditMessageCreator.withoutKeyboard(chatId, replyId,
                        "_У сервера нет данных о размере книги_"));
                return -1;
            }
        }
    }

    /**
     * Если нет доступа к сети tor, то пробую через зеркало.
     * Если и к зеркалу нет доступа, то пробрасываю исключение IOException
     */
    private BufferedInputStream initializeInputStream(long chatId,
                                                      int replyId,
                                                      String fileId,
                                                      TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        try {
            bot.execute(withoutKeyboard(chatId, replyId,
                    "_Попытка установки соединения через TOR_"));
            val conn = this.openConnection(fileId, "GET", this.createProxy());
            val bis = new BufferedInputStream(conn.getInputStream());
            bot.execute(withoutKeyboard(chatId, replyId,
                    "_TOR соединение успешно установленно_"));
            return bis;
        } catch (IOException e) {
            try {
                LoggerUtils.logStackTrace(e);
                bot.execute(withoutKeyboard(chatId, replyId,
                        "_Попытка установки соединения через зеркало_"));
                val conn = this.openConnection(fileId, "GET", null);
                val bis = new BufferedInputStream(conn.getInputStream());
                bot.execute(withoutKeyboard(chatId, replyId,
                        "_Соединение успешно установленно_"));
                return bis;
            } catch (IOException ex) {
                throw new IOException("Не могу установить соединение c библиотекой. Попробуйте позже", ex);
            }
        }
    }

    private FileOutputStream initializeOutputStream(Path pathToArchive) throws FileNotFoundException {
        try {
            return new FileOutputStream(pathToArchive.toFile());
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("Не могу скопировать книгу. Попробуйте позже");
        }
    }

    private void download(long chatId,
                          int replyId,
                          BufferedInputStream bis,
                          FileOutputStream fos,
                          TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        bot.execute(withoutKeyboard(chatId, replyId,
                "_Начинаю загрузку книги_"));

        bis.transferTo(fos);

        bot.execute(withoutKeyboard(chatId, replyId,
                "*↓ Книга успешно загружена ↓*"));
    }

    private void downloadWithTrace(long chatId,
                                   int replyId,
                                   long fileSize,
                                   BufferedInputStream bis,
                                   FileOutputStream fos,
                                   TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        bot.execute(withoutKeyboard(chatId, replyId,
                "_Начинаю загрузку книги_"));

        byte[] buffer;
        val percent = fileSize / 100;
        for (int i = 0; i <= 100; i++) {
            buffer = bis.readNBytes((int) (percent));
            fos.write(buffer);
            bot.execute(withoutKeyboard(chatId, replyId, format("_Загружено: %d%%_", i)));
        }

        bot.execute(withoutKeyboard(chatId, replyId,
                "*↓ Книга успешно загружена ↓*"));
    }

    private HttpURLConnection openConnection(String fileId, String method, Proxy proxy) throws IOException {
        if (proxy != null) {
            val conn = (HttpURLConnection) new URL(format("%s%s", ONION_URL.getURL(), fileId)).openConnection(proxy);
            conn.setRequestMethod(method);
            return conn;
        } else {
            val conn = (HttpURLConnection) new URL(format("%s%s", SITE_URL.getURL(), fileId)).openConnection();
            conn.setRequestMethod(method);
            return conn;
        }
    }

    private Proxy createProxy() {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(HOST, PORT));
    }
}
