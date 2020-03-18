package com.dreamfoxick.telegrambot.services.message.creator;

import lombok.val;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.message.handler.CommandButtonText.*;
import static java.util.stream.Collectors.toUnmodifiableList;

public class KeyboardCreator {
    private static final ReplyKeyboardMarkup backKeyboard;
    private static final ReplyKeyboardMarkup mainKeyboard;

    static {
        backKeyboard = createReply(
                Collections.singletonList(
                        Collections.singletonList(BACK.getButtonText())));

        val buttons = new ArrayList<String>();
        buttons.add(BOOK.getButtonText());
        buttons.add(AUTHOR.getButtonText());
        mainKeyboard = createReply(Collections.singletonList(buttons));
    }

    /**
     * Базовые реализации
     */
    public static ReplyKeyboardMarkup backKeyboard() {
        return backKeyboard;
    }

    public static ReplyKeyboardMarkup mainKeyboard() {
        return mainKeyboard;
    }

    /**
     * Создает reply клавиатуру
     *
     * @param buttons - лист уровней, в каждом уровне лист кнопок
     */
    public static ReplyKeyboardMarkup createReply(List<List<String>> buttons) {
        val keyboard = new ReplyKeyboardMarkup();
        val rows = buttons.stream()
                .map(KeyboardCreator::createReplyKeyboardRow)
                .collect(toUnmodifiableList());
        keyboard.setKeyboard(rows);
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        return keyboard;
    }

    /**
     * Создает inline клавиатуур
     *
     * @param buttons - массив уровней, в каждом уровне map кнопок (key -> текст кнопки, value -> callback query)
     */
    public static InlineKeyboardMarkup createInline(List<Map<String, String>> buttons) {
        val keyboard = new InlineKeyboardMarkup();
        val rows = buttons.stream()
                .map(KeyboardCreator::createInlineKeyboardRow)
                .collect(toUnmodifiableList());
        keyboard.setKeyboard(rows);
        return keyboard;
    }


    private static List<InlineKeyboardButton> createInlineKeyboardRow(Map<String, String> buttons) {
        return buttons.entrySet()
                .stream()
                .map(e -> createInlineButton(e.getKey(), e.getValue()))
                .collect(toUnmodifiableList());
    }

    private static KeyboardRow createReplyKeyboardRow(List<String> buttons) {
        val row = new KeyboardRow();
        row.addAll(buttons);
        return row;
    }

    private static InlineKeyboardButton createInlineButton(String text,
                                                           String callbackData) {
        val b = new InlineKeyboardButton();
        b.setText(text);
        b.setCallbackData(callbackData);
        return b;
    }
}
