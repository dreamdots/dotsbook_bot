package com.dreamfoxick.telegrambot.services.message.executor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommandResponseText {
    START("*Привет!*\n\nС моей помощью вы можете скачать любую книгу из библиотеки Flibusta.\n\nВыберите критерий поиска, нажав соответствующую кнопку на клавиатуре" +
            ", и следуйте дальнейшим инструкциям."),
    BOOK("Введите полное или частичное название книги\n_Пример: Бесы_"),
    AUTHOR("Введите ФИО автора\n_Пример: Карл Густав Юнг_"),
    BACK("Выберите критерий поиска"),
    DEFAULT("Вы не выбрали критерий поиска");

    @Getter
    private final String responseText;
}
