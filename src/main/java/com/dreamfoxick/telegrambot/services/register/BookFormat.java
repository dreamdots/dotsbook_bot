package com.dreamfoxick.telegrambot.services.register;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookFormat {
    FB2("fb2"),
    EPUB("epub"),
    MOBI("mobi"),
    DOC("doc"),
    PDF("pdf"),
    DJVI("djvi");

    @Getter
    private final String format;
}
