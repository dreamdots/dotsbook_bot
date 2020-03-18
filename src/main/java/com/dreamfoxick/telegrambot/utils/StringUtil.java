package com.dreamfoxick.telegrambot.utils;

public class StringUtil {
    private StringUtil() {
    }

    public static String insertSlash(String link) {
        return new StringBuffer(link).insert(2, "/").toString();
    }
}
