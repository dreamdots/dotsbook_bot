package com.dreamfoxick.telegrambot.services.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RegEx {
    BOOK_LINK("/b/[\\d]+"),
    AUTHOR_LINK("/a/[\\d]+"),

    /**
     * Для работы searcher
     */
    FB2_FORMAT_LINK("/b/[\\d]+/fb2"),
    EPUB_FORMAT_LINK("/b/[\\d]+/epub"),
    MOBI_FORMAT_LINK("/b/[\\d]+/mobi"),
    PDF_DJVI_DOC_FORMAT_LINK("/b/[\\d]+/download"),
    BOOK_AND_ANY_FORMAT_LINK("/b/[\\d]+/(fb2|epub|mobi|download)"),

    /**
     * Для проверки входящих сообщений
     */
    BOOK_LINK_REQUEST("/b[\\d]+"),
    AUTHOR_LINK_REQUEST("/a[\\d]+"),

    /**
     * Удалить второй / в ссылке
     */
    BOOK_LINK_SECOND_SLASH("(?<=b)/"),
    AUTHOR_LINK_SECOND_SLASH("(?<=a)/"),

    /**
     * Для обработки queries
     */
    DOWNLOAD_QUERY("[\\d]+_/b/[\\d]+_[\\w]+"),
    UPDATE_QUERY("(next|back)_[\\d]{1,3}");

    @Getter
    private final String regEx;
}
