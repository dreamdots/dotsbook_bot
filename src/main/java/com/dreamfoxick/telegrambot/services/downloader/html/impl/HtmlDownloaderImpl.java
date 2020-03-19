package com.dreamfoxick.telegrambot.services.downloader.html.impl;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Component
public class HtmlDownloaderImpl implements HtmlDownloader {
    private final HttpClient client = HttpClients.createMinimal();

    @Override
    public String download(String URL) throws IOException {
        try {
            val req = new HttpGet(URL);
            log.debug(format("Created http get request for URL: %s", URL));
            val res = client.execute(req);
            log.debug("Request successfully executed");
            val entity = res.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException ex) {
            throw new IOException("Не могу установить соединение с библиотекой. Попробуйте позже", ex);
        }
    }
}
