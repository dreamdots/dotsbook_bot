package com.dreamfoxick.telegrambot.services.message.creator;

import com.dreamfoxick.telegrambot.services.enums.CallbackData;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;
import java.util.HashMap;

import static java.lang.String.format;

public class EditMessageCreator {
    private final static String NEXT_BUTTON_TEXT = "→";
    private final static String BACK_BUTTON_TEXT = "←";

    /**
     * Кнопка Далее
     */
    public static EditMessageText createEditMessageWithInlineKeyboardNextButton(long chatId,
                                                                                int replyId,
                                                                                String text,
                                                                                int callbackIndex) {
        return createOneButtonMessage(chatId, replyId, text, NEXT_BUTTON_TEXT, format("%s%d", CallbackData.NEXT.getCallback(), callbackIndex));
    }

    /**
     * Кнопка Назад
     */
    public static EditMessageText createEditMessageWithInlineKeyboardBackButton(long chatId,
                                                                                int replyId,
                                                                                String text,
                                                                                int callbackIndex) {

        return createOneButtonMessage(chatId, replyId, text, BACK_BUTTON_TEXT, format("%s%d", CallbackData.BACK.getCallback(), callbackIndex));
    }

    /**
     * Кнопки Назад и Далее
     */
    public static EditMessageText createEditMessageWithInlineKeyboardCombineButtons(long chatId,
                                                                                    int replyId,
                                                                                    String text,
                                                                                    int callbackIndex) {
        val buttons = new HashMap<String, String>();
        buttons.put(BACK_BUTTON_TEXT, format("%s%d", CallbackData.BACK.getCallback(), callbackIndex));
        buttons.put(NEXT_BUTTON_TEXT, format("%s%d", CallbackData.NEXT.getCallback(), callbackIndex));
        return createEditMessageWithInlineKeyboard(chatId, replyId, text, KeyboardCreator.createInline(Collections.singletonList(buttons)));
    }

    /**
     * Сообщение с любой inline клавиатурой
     */
    public static EditMessageText createEditMessageWithInlineKeyboard(long chatId,
                                                                      int replyId,
                                                                      String text,
                                                                      InlineKeyboardMarkup keyboard) {
        return withKeyboard(chatId, replyId, text, keyboard);
    }


    public static EditMessageText withoutKeyboard(long chatId,
                                                  int replyId,
                                                  String text) {
        val editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(replyId);
        editMessage.setText(text);
        editMessage.disableWebPagePreview();
        editMessage.enableMarkdown(true);
        return editMessage;
    }

    private static EditMessageText createOneButtonMessage(long chatId,
                                                          int replyId,
                                                          String text,
                                                          String buttonText,
                                                          String callbackData) {
        val button = new HashMap<String, String>();
        button.put(buttonText, callbackData);
        return createEditMessageWithInlineKeyboard(chatId, replyId, text, KeyboardCreator.createInline(Collections.singletonList(button)));
    }

    private static EditMessageText withKeyboard(long chatId,
                                                int replyId,
                                                String text,
                                                InlineKeyboardMarkup keyboard) {
        val message = withoutKeyboard(chatId, replyId, text);
        message.setReplyMarkup(keyboard);
        return message;
    }
}
