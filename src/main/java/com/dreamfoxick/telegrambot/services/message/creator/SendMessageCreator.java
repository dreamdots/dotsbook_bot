package com.dreamfoxick.telegrambot.services.message.creator;

import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class SendMessageCreator {

    /**
     * Сообщение с любой reply клавиатурой
     */
    public static SendMessage createSendMessageWithReplyKeyboard(long chatId,
                                                                 String text,
                                                                 ReplyKeyboardMarkup keyboard) {
        return withKeyboard(chatId, text, keyboard);
    }

    /**
     * Сообщение с любой inline клавиатурой
     */
    public static SendMessage createSendMessageWithInlineKeyboard(long chatId,
                                                                  String text,
                                                                  InlineKeyboardMarkup keyboard) {
        return withKeyboard(chatId, text, keyboard);
    }

    /**
     * Сообщение без reply клавиатуры
     */
    public static SendMessage createSendMessageWithoutKeyboard(long chatId,
                                                               String text) {
        val message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableNotification();
        message.disableWebPagePreview();
        message.enableMarkdown(true);
        return message;
    }

    private static SendMessage withKeyboard(long chatId,
                                            String text,
                                            ReplyKeyboard keyboard) {
        val message = createSendMessageWithoutKeyboard(chatId, text);
        message.setReplyMarkup(keyboard);
        return message;
    }
}
