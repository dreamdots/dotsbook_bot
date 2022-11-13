package com.dreamfoxick.telegrambot.services.downloader.file.impl;

import com.dreamfoxick.telegrambot.services.downloader.file.FileDownloader;
import com.dreamfoxick.telegrambot.services.enums.URLConstant;
import com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.dreamfoxick.telegrambot.services.message.creator.EditMessageCreator.withoutKeyboard;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bot.properties")
public class MultiFileDownloader implements FileDownloader {

  private final List<URLConstant> mirrors = List.of(URLConstant.SITE_URL);
  private final HttpClient client = HttpClientBuilder
      .create()
      .build();
  @Value("${temp_files_directory}")
  private Path TEMP_DIRECTORY;

  @Override
  public File download(long chatId,
      String fileId,
      TelegramLongPollingBot bot) throws IOException, TelegramApiException {
    val pathToArchive = this.createZipArchive(chatId);
    val replyId = /*this.sendHeadingMessage(chatId, bot);*/ 0;

    val pair = this.initializeInputStream(chatId, replyId, fileId, bot);
    try (val os = this.initializeOutputStream(pathToArchive)) {
      val size = pair.getKey();

      try (val is = pair.getValue();) {
//        bot.execute(withoutKeyboard(chatId, replyId, "_Скачивание книги_"));

        if (size == null || size == 0) {
          IOUtils.copy(is, os);
        } else {
          byte[] buffer;
          val percent = pair.getKey() / 100;
          for (int i = 0; i <= 100; i++) {
            buffer = is.readNBytes((int) (percent));
            os.write(buffer);
            try {
//              bot.execute(withoutKeyboard(chatId, replyId, format("_Загружено: %d%%_", i)));
            } catch (final Exception ignored) {}
          }
        }
      }
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
//  private int sendHeadingMessage(long chatId,
//      TelegramLongPollingBot bot) throws TelegramApiException {
//    return bot.execute(SendMessageCreator.createSendMessageWithoutKeyboard(chatId,
//        "_Установка соединения с библиотекой_")).getMessageId();
//  }

  /**
   * Если нет доступа к сети tor, то пробую через зеркало. Если и к зеркалу нет доступа, то
   * пробрасываю исключение IOException
   */
  private Pair<Long, InputStream> initializeInputStream(long chatId,
      int replyId,
      String fileId,
      TelegramLongPollingBot bot) throws TelegramApiException, IOException {
    try {
//      bot.execute(withoutKeyboard(chatId, replyId, "_Установка соединения c сервером_"));
      return this.openConnection(chatId, URLConstant.IS_URL, replyId, fileId, bot);
    } catch (IOException e) {
      for (URLConstant mirror : mirrors) {
        try {
//          bot.execute(withoutKeyboard(chatId, replyId,
//              "_Установка соединения через " + mirror.name().toLowerCase().replaceAll("_url", "")
//                  + " зеркало_"));
          return this.openConnection(chatId, mirror, replyId, fileId, bot);
        } catch (final IOException ignored) {
        }
      }

      throw new IOException("Не могу установить соединение c библиотекой. Попробуйте позже", e);
    }
  }

  private BufferedOutputStream initializeOutputStream(Path pathToArchive)
      throws FileNotFoundException {
    try {
      return new BufferedOutputStream(new FileOutputStream(pathToArchive.toFile()));
    } catch (FileNotFoundException ex) {
      throw new FileNotFoundException("Не могу скопировать книгу. Попробуйте позже");
    }
  }

  @SneakyThrows
  private Pair<Long, InputStream> openConnection(long chatId,
      URLConstant constant,
      int replyId,
      String fileId,
      TelegramLongPollingBot bot) throws IOException {
    var request = new HttpGet();
    request.setURI(new URI(format("%s%s", constant.getURL(), fileId)));
    var response = client.execute(request);
//    bot.execute(withoutKeyboard(chatId, replyId, "_Подготовка к скачиванию_"));

    if (response.getStatusLine().getStatusCode() == 302 && response.containsHeader("Location")) {
//      bot.execute(withoutKeyboard(chatId, replyId, "_Поиск книги на другом сервере_"));
      request = new HttpGet(response.getFirstHeader("Location").getValue());
      val entity = client.execute(request).getEntity();
      return Pair.of(entity.getContentLength(), entity.getContent());
    } else {
      val entity = response.getEntity();
      return Pair.of(entity.getContentLength(), entity.getContent());
    }
  }
}
