package com.dreamfoxick.telegrambot.services.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings("JavadocReference")
public enum Pattern {
    /**
     * Паттерны для ключей queries map
     *
     * @see com.dreamfoxick.telegrambot.services.register.impl.QueryRegisterImpl#buildDownloadButton(long, String, String, Map)
     */
    DOWNLOAD_QUERY_CALLBACK_DATA("%s_%s_"),
    UPDATE_QUERY_CALLBACK_DATA("%d_%d_"),

    /**
     * Паттерны для составления ответа пользователю
     *
     * @see com.dreamfoxick.telegrambot.services.message.processor.impl.ul.AbstractULCommandProcessor#getLinks(Map)
     */
    RESPONSE_LINK("_ссылка: %s_"),
    /**
     * @see com.dreamfoxick.telegrambot.services.message.processor.impl.ul.AuthorsCommandProcessor#getProfiles(Map)
     */
    RESPONSE_BOOK_NAME("*%s*"),
    /**
     * @see com.dreamfoxick.telegrambot.services.message.processor.impl.ul.AuthorsCommandProcessor#getProfiles(Map)
     */
    RESPONSE_AUTHOR_FULL_NAME("*%s*"),
    /**
     * @see com.dreamfoxick.telegrambot.services.message.processor.impl.ul.BooksCommandProcessor#getProfiles(Map)
     */
    RESPONSE_BOOK_NAME_AND_AUTHOR_FULL_NAME("*%s*\n_автор: %s_");

    @Getter
    private final String pattern;
}
