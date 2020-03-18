package com.dreamfoxick.telegrambot.services.message.creator;

import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;

public class SendDocumentCreator {

    public static SendDocument createWithoutReplyKeyboard(long chatId,
                                                          File sourceFile) {
        val sendDocument = new SendDocument();

        sendDocument.setDocument(sourceFile);
        sendDocument.setChatId(chatId);
        return sendDocument;
    }

    public static SendDocument createSendDocumentWithReplyKeyboard(long chatId,
                                                                   File sourceFile,
                                                                   ReplyKeyboardMarkup keyboard) {
        val sendDocument = createWithoutReplyKeyboard(chatId, sourceFile);
        sendDocument.setReplyMarkup(keyboard);
        return sendDocument;
    }
}
