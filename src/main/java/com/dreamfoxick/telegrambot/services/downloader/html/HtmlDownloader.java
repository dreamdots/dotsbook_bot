package com.dreamfoxick.telegrambot.services.downloader.html;

import java.io.IOException;

public interface HtmlDownloader {

    String download(String URL) throws IOException;

}
