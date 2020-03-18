package com.dreamfoxick.telegrambot.configuration.threadfactory;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CustomThreadFactory implements ThreadFactory {
    private final String poolName;
    private final String threadName;

    private final AtomicInteger created;
    private final AtomicInteger alive;

    private final String threadStartMessage;
    private final String threadExitMessage;


    @Override
    public Thread newThread(@Nonnull Runnable r) {
        return new CustomThread(r,
                poolName,
                threadName,
                threadStartMessage,
                threadExitMessage,
                created,
                alive);
    }
}
