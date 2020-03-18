package com.dreamfoxick.telegrambot.configuration.threadfactory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

@Slf4j
public class CustomThread extends Thread {
    private final AtomicInteger alive;

    private final String threadStartMessage;
    private final String threadExitMessage;

    public CustomThread(Runnable r,
                        String poolName,
                        String threadName,
                        String threadStartMessage,
                        String threadExitMessage,
                        AtomicInteger created,
                        AtomicInteger alive) {
        super(r, format("%s_%s-%d", poolName, threadName, created.incrementAndGet()));
        this.threadStartMessage = threadStartMessage;
        this.threadExitMessage = threadExitMessage;
        this.alive = alive;
    }


    @Override
    public void run() {
        try {
            alive.incrementAndGet();
            log.debug(format(threadStartMessage, this.getName(), alive.get()));
            super.run();
        } finally {
            log.debug(format(threadExitMessage, this.getName(), alive.get()));
            alive.decrementAndGet();
        }
    }
}
