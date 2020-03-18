package com.dreamfoxick.telegrambot.services.searcher;

import com.dreamfoxick.telegrambot.services.enums.URLConstant;
import com.dreamfoxick.telegrambot.utils.annotation.ReadyToUse;

import java.io.IOException;
import java.util.Map;

@ReadyToUse
public interface Searcher {

    /**
     * @throws IOException              - ошибка устанвки соединения
     * @throws IllegalArgumentException - по параметру ничего не найдено или он неверный
     */
    Map<String, String[]> search(String param,
                                 URLConstant URLType) throws IOException, IllegalArgumentException;
}
