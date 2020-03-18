package com.dreamfoxick.telegrambot.services.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum URLConstant {
    FIND_BOOKS_URL("http://flibusta.site/booksearch?ask=PARAM&chb=on"),
    FIND_AUTHORS_URL("http://flibusta.site/booksearch?ask=PARAM&cha=on"),
    BASE_SITE_URL("http://flibusta.site");

    @Getter
    private final String URL;
}
