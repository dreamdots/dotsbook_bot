package com.dreamfoxick.telegrambot.services.message.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommandButtonText {
    START("/start"),
    BOOK("По названию книги"),
    AUTHOR("По ФИО автора"),
    BACK("Главное меню");

    @Getter
    private final String buttonText;
}
