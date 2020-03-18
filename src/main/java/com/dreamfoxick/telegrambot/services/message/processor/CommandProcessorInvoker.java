package com.dreamfoxick.telegrambot.services.message.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Service
public class CommandProcessorInvoker {

    private final CommandProcessor findBooksCommandProcessor;
    private final CommandProcessor findAuthorsCommandProcessor;
    private final CommandProcessor findAuthorCommandProcessor;
    private final CommandProcessor findBookCommandProcessor;

    public CommandProcessorInvoker(@Qualifier("booksCommandProcessor") CommandProcessor findBooksCommandProcessor,
                                   @Qualifier("authorsCommandProcessor") CommandProcessor findAuthorsCommandProcessor,
                                   @Qualifier("authorCommandProcessor") CommandProcessor findAuthorCommandProcessor,
                                   @Qualifier("bookCommandProcessor") CommandProcessor findBookCommandProcessor) {
        this.findBooksCommandProcessor = findBooksCommandProcessor;
        this.findAuthorsCommandProcessor = findAuthorsCommandProcessor;
        this.findAuthorCommandProcessor = findAuthorCommandProcessor;
        this.findBookCommandProcessor = findBookCommandProcessor;
    }

    private static void log(long chatId,
                            String param,
                            String methodName) {
        log.info(format("Called method: '%s' with param: {%d, %s}", methodName, chatId, param));
    }

    public void processFindBooks(long chatId,
                                 String bookName,
                                 TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        log(chatId, bookName, Thread.currentThread().getStackTrace()[1].getMethodName());
        findBooksCommandProcessor.process(chatId, bookName, bot);
    }

    public void processFindAuthors(long chatId,
                                   String authorName,
                                   TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        log(chatId, authorName, Thread.currentThread().getStackTrace()[1].getMethodName());
        findAuthorsCommandProcessor.process(chatId, authorName, bot);
    }

    public void processFindAuthor(long chatId,
                                  String authorLink,
                                  TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        log(chatId, authorLink, Thread.currentThread().getStackTrace()[1].getMethodName());
        findAuthorCommandProcessor.process(chatId, authorLink, bot);
    }

    public void processFindBook(long chatId,
                                String bookLink,
                                TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        log(chatId, bookLink, Thread.currentThread().getStackTrace()[1].getMethodName());
        findBookCommandProcessor.process(chatId, bookLink, bot);
    }
}

