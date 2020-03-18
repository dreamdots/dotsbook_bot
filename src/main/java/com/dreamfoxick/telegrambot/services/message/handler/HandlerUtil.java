package com.dreamfoxick.telegrambot.services.message.handler;

import lombok.val;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.lang.Character.UnicodeBlock.*;
import static java.util.stream.IntStream.range;

public class HandlerUtil {
    private HandlerUtil() {
    }

    public static boolean isTextMessage(Message m) {
        if (m.hasText()) {
            val ch = m.getText().toCharArray();
            return range(0, ch.length)
                    .allMatch(i -> (of(ch[i]) == CYRILLIC) || (of(ch[i]) == BASIC_LATIN));
        } else return false;
    }

    public static boolean isCallbackQueryUpdate(Update u) {
        return u.hasCallbackQuery();
    }

    public static boolean updateHasMessage(Update u) {
        return u.hasMessage();
    }
}
