package com.dreamfoxick.telegrambot.services.register.impl;

import com.dreamfoxick.telegrambot.services.enums.Pattern;
import com.dreamfoxick.telegrambot.services.register.QueryRegister;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static com.dreamfoxick.telegrambot.services.enums.Pattern.DOWNLOAD_QUERY_CALLBACK_DATA;
import static com.dreamfoxick.telegrambot.services.enums.Pattern.UPDATE_QUERY_CALLBACK_DATA;
import static com.dreamfoxick.telegrambot.services.enums.RegEx.*;
import static com.dreamfoxick.telegrambot.services.message.creator.EditMessageCreator.*;
import static com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator.backKeyboard;
import static com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator.createInline;
import static com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator.*;
import static com.dreamfoxick.telegrambot.services.register.BookFormat.*;
import static com.dreamfoxick.telegrambot.utils.StringUtil.insertSlash;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueryRegisterImpl implements QueryRegister {
    private final StateController<String, Map<Integer, EditMessageText>> updateQueryController;
    private final StateController<String, String> downloadQueryController;


    /**
     * Алгоритм работы:
     * 1) Отправить обычное сообщение с reply клавиатурой
     * 2) Получить id отправленного сообщения
     * 3) Построить необходимое количество edit message с кнопками
     * 4) Забиндить все edit message по ключу chatId_replyId_ в updateQueryController
     * 5) Добавить inline клавиатуру к отправленному сообщению
     */
    @Override
    public void registerUpdateQueries(long chatId,
                                      List<String> response,
                                      TelegramLongPollingBot bot) throws TelegramApiException {
        // Первая страничка результатов
        val firstPage = response.get(0);
        if (response.size() == 1) {
            // Если страничка одна, то отправляю обычное сообщение
            this.executeNonInlineMessage(chatId, firstPage, bot);
        } else {
            // Inline ответ
            // Отправляю обычное сообщение, чтобы получить id
            val replyId = this.executeFirstCommonMessage(chatId, firstPage, bot);
            log.debug(format("First message sent, replyId: %d_%d", chatId, replyId));

            // Первое сообщение с кнопкой Далее
            val firstMessage = this.buildFirstMessage(chatId, replyId, response);
            log.debug(format("First message with button 'Далее' created: %d_%d", chatId, firstMessage.getMessageId()));
            // Последнее сообщение с кнопкой Назад
            val lastMessage = this.buildLastMessage(chatId, replyId, response);
            log.debug(format("Last message with button 'Назад' created: %d_%d", chatId, lastMessage.getMessageId()));

            // Если страничек с результатом больше двух, то обрабатываю все другие странички
            val middleMessages = this.buildCombineMessage(chatId, replyId, response);

            // Собираю все сообщения в массив
            val allMessages = new ArrayList<EditMessageText>();
            allMessages.add(firstMessage);
            if (middleMessages.size() != 0) {
                allMessages.addAll(middleMessages);
                log.debug("All middle messages added");
            }
            allMessages.add(lastMessage);

            // Создаю мапу с edit message с кнопками
            val queries = this.buildQueriesMap(allMessages);

            /*
             Регистрирую каждую кнопку каждого сообщения
             callbackData -> chatId_replyId_next|back_index
             */
            this.registerUpdateButtons(chatId, replyId, queries);

            // Добавляю кнопки к отправленному сообщению
            bot.execute(firstMessage);
            log.debug("Message sent");
        }
    }


    /**
     * Алгоритм работы:
     * 1) По ссылкам в ответе создаю клавиатуру
     * 2) Регистрирую каждую кнопку клавиатуры по ключу chatId_bookId_format
     * 3) Отправляю сообщение с inline кнопками
     */
    @Override
    public void registerDownloadQueries(long chatId,
                                        String bookId,
                                        List<String> response,
                                        TelegramLongPollingBot bot) throws TelegramApiException {
        // создаю массив кнопок и регистирую их
        val buttons = this.buildAndRegisterDownloadButtons(chatId, bookId, response);
        log.debug(format(
                "Registered download queries: %s",
                buttons.entrySet().stream()
                        .map(e -> format("\nkey: %d_%s_%s \nvalue: %s", chatId, bookId, e.getKey(), e.getValue()))
                        .collect(joining("\n"))));

        // создаю клавиатуру с inline кнопками для скачивания
        val keyboard = createInline(Collections.singletonList(buttons));
        log.debug("Keyboard with download buttons created");

        // отправляю сообщение
        bot.execute(createSendMessageWithInlineKeyboard(
                chatId,
                "*Выберите формат*",
                keyboard));
        log.debug("Message sent");
    }

    private void executeNonInlineMessage(long chatId,
                                         String response,
                                         TelegramLongPollingBot bot) throws TelegramApiException {
        bot.execute(createSendMessageWithReplyKeyboard(chatId, response, backKeyboard()));
    }

    private int executeFirstCommonMessage(long chatId,
                                          String response,
                                          TelegramLongPollingBot bot) throws TelegramApiException {
        return bot.execute(createSendMessageWithoutKeyboard(chatId, response)).getMessageId();
    }

    private EditMessageText buildFirstMessage(long chatId,
                                              int replyId,
                                              List<String> response) {
        return createEditMessageWithInlineKeyboardNextButton(chatId, replyId, response.get(0), 0);
    }

    private EditMessageText buildLastMessage(long chatId,
                                             int replyId,
                                             List<String> response) {
        return createEditMessageWithInlineKeyboardBackButton(chatId, replyId, response.get(response.size() - 1), response.size() - 1);
    }

    /**
     * Для всех страничек результата (кроме первой и последней) добавляю кнопки Назад и Далее
     */
    private List<EditMessageText> buildCombineMessage(long chatId,
                                                      int replyId,
                                                      List<String> response) {
        val middleMessages = new ArrayList<EditMessageText>();
        for (int i = 1; i < response.size() - 1; i++) {
            middleMessages.add(createEditMessageWithInlineKeyboardCombineButtons(chatId, replyId, response.get(i), i));
        }
        return middleMessages;
    }

    /**
     * Создаю мапу edit сообщений
     */
    private Map<Integer, EditMessageText> buildQueriesMap(List<EditMessageText> messages) {
        return range(0, messages.size())
                .boxed()
                .collect(toMap(i -> i, messages::get));
    }

    /**
     * Регистрирую мапу сообщений по ключу chatId_replyId_
     */
    private void registerUpdateButtons(long chatId,
                                       int replyId,
                                       Map<Integer, EditMessageText> queries) {
        val updateQueryKey = format(UPDATE_QUERY_CALLBACK_DATA.getPattern(), chatId, replyId);
        updateQueryController.addIfAbsent(updateQueryKey, queries);
        log.debug(format(
                "Registered update queries: \nkey -> %s \nvalues: \n%s", updateQueryKey,
                queries.entrySet().stream()
                        .map(e -> format("key: %d, messageId: %s", e.getKey(), e.getValue().getMessageId().toString()))
                        .collect(joining("\n"))));
    }

    /**
     * Для каждой ссылке в массиве response создаю кнопку
     *
     * @see QueryRegisterImpl#buildDownloadButton(long, String, String, Map)
     */
    private Map<String, String> buildAndRegisterDownloadButtons(long chatId,
                                                                String bookId,
                                                                List<String> response) {
        val buttons = new HashMap<String, String>();
        for (String formatLink : response) {
            if (formatLink.matches(FB2_FORMAT_LINK.getRegEx())) {
                this.buildDownloadButton(chatId, bookId, FB2.getFormat(), buttons);
            } else if (formatLink.matches(EPUB_FORMAT_LINK.getRegEx())) {
                this.buildDownloadButton(chatId, bookId, EPUB.getFormat(), buttons);
            } else if (formatLink.matches(MOBI_FORMAT_LINK.getRegEx())) {
                this.buildDownloadButton(chatId, bookId, MOBI.getFormat(), buttons);
            } else {
                this.buildDownloadButton(chatId, bookId, "download", buttons);
            }
        }
        return buttons;
    }

    /**
     * Создаю и регистрирую кнопку клавиатуры с callbackData -> chatId_bookId_format
     *
     * @see QueryRegisterImpl#buildDownloadQueryCallbackData(long, String, String)
     */
    private void buildDownloadButton(long chatId,
                                     String bookId,
                                     String format,
                                     Map<String, String> buttons) {
        val callbackData = this.buildDownloadQueryCallbackData(chatId, bookId, format);
        buttons.put(format, callbackData);
        this.registerDownloadButton(callbackData, bookId, format);
    }

    /**
     * Связываю с callbackData ссылку для скачивания
     */
    private void registerDownloadButton(String callbackData,
                                        String bookId,
                                        String format) {
        downloadQueryController.addIfAbsent(callbackData, this.buildDownloadURL(bookId, format));
    }

    /**
     * Pattern: chatId_bookId_format
     *
     * @see Pattern#DOWNLOAD_QUERY_CALLBACK_DATA
     */
    private String buildDownloadQueryCallbackData(long chatId,
                                                  String bookId,
                                                  String format) {
        return format(
                // %s%s -> %s_%s_format
                format("%s%s", DOWNLOAD_QUERY_CALLBACK_DATA.getPattern(), format),
                chatId,
                bookId);
    }

    /**
     * Восстанавливаю URL для скачивания
     */
    private String buildDownloadURL(String bookId,
                                    String format) {
        bookId = insertSlash(bookId);
        return format("%s/%s", bookId, format);
    }
}
