package com.dreamfoxick.telegrambot.services.message.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.String.format;

@Slf4j
@Service
public class CommandExecutorInvoker {
    private final CommandExecutor start;
    private final CommandExecutor back;
    private final CommandExecutor book;
    private final CommandExecutor author;
    private final CommandExecutor defaultExec;

    @Autowired
    public CommandExecutorInvoker(@Qualifier("startCommandExecutor") CommandExecutor start,
                                  @Qualifier("backCommandExecutor") CommandExecutor back,
                                  @Qualifier("bookCommandExecutor") CommandExecutor book,
                                  @Qualifier("authorCommandExecutor") CommandExecutor author,
                                  @Qualifier("defaultCommandExecutor") CommandExecutor defaultExec) {
        this.start = start;
        this.back = back;
        this.book = book;
        this.author = author;
        this.defaultExec = defaultExec;
    }

    private static void log(long chatId,
                            String methodName) {
        log.info(format("Called method: '%s' with param: %d", methodName, chatId));
    }

    public void startCommand(long chatId,
                             TelegramLongPollingBot bot) throws TelegramApiException {
        log(chatId, Thread.currentThread().getStackTrace()[1].getMethodName());
        start.execute(chatId, bot);
    }

    public void bookCommand(long chatId,
                            TelegramLongPollingBot bot) throws TelegramApiException {
        log(chatId, Thread.currentThread().getStackTrace()[1].getMethodName());
        book.execute(chatId, bot);
    }

    public void authorCommand(long chatId,
                              TelegramLongPollingBot bot) throws TelegramApiException {
        log(chatId, Thread.currentThread().getStackTrace()[1].getMethodName());
        author.execute(chatId, bot);
    }

    public void backCommand(long chatId,
                            TelegramLongPollingBot bot) throws TelegramApiException {
        log(chatId, Thread.currentThread().getStackTrace()[1].getMethodName());
        back.execute(chatId, bot);
    }

    public void defaultCommand(long chatId,
                               TelegramLongPollingBot bot) throws TelegramApiException {
        log(chatId, Thread.currentThread().getStackTrace()[1].getMethodName());
        defaultExec.execute(chatId, bot);
    }
}
