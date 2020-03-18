package com.dreamfoxick.telegrambot.configuration.threadfactory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Slf4j
public class ExecutorUtils {
    private ExecutorUtils() {

    }

    public static void logExecutorCharacters(ThreadPoolExecutor exec,
                                             String execName) {
        log.info(format("\n\nCreated %s executor:\nCorePoolSize: %d\nMaxPoolSize: %d\nKeepAliveTime: %d" +
                        "\nTimeUnit: %s\nQueueSize: %d\nThreadFactory: %s\nRejectedHandler: %s\n",
                execName,
                exec.getCorePoolSize(),
                exec.getMaximumPoolSize(),
                exec.getKeepAliveTime(TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS.toString(),
                exec.getQueue().remainingCapacity(),
                exec.getThreadFactory().getClass().getName(),
                exec.getRejectedExecutionHandler().getClass().getName()));
    }
}
