package com.dreamfoxick.telegrambot.services.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum URLConstant {
    FIND_BOOKS_URL("http://flibusta.is/booksearch?ask=PARAM&chb=on"),
    FIND_AUTHORS_URL("http://flibusta.is/booksearch?ask=PARAM&cha=on"),
    SITE_URL("http://flibusta.site"),
    IS_URL("http://flibusta.is");

    @Getter
    private final String URL;
}
