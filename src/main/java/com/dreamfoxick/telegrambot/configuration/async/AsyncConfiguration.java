package com.dreamfoxick.telegrambot.configuration.async;

import com.dreamfoxick.telegrambot.configuration.threadfactory.CustomThreadFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dreamfoxick.telegrambot.configuration.threadfactory.ExecutorUtils.logExecutorCharacters;

@Slf4j
@Configuration
@PropertySource("classpath:bot.properties")
public class AsyncConfiguration {

    @Bean(name = "longPollExec")
    public Executor longPollExec() {
        val runtime = Runtime.getRuntime();
        val exec = new ThreadPoolExecutor(
                // целевой размер пула
                runtime.availableProcessors() + 20,
                // максимальный размер пула
                runtime.availableProcessors() + 21,
                // время, которое нужно прождать потоку в бейздействии до уничтожения
                Short.MAX_VALUE,
                // TimeUnit для предыдущего значения
                TimeUnit.MILLISECONDS,
                // очередь задач размером Short.MAX_VALUE
                new ArrayBlockingQueue<>(Short.MAX_VALUE),
                // кастомная фабрика потоков для именования и ведения статистики
                new CustomThreadFactory(
                        "async",
                        "worker",
                        new AtomicInteger(0),
                        new AtomicInteger(0),
                        "Startup long poll thread: %s. Alive thread: %d",
                        "Exiting long poll thread: %s. Alive thread: %d"),
                // при переполнении очереди пул выполняет задачу в вызывающем потоке
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        logExecutorCharacters(exec, "longPollExec");
        exec.prestartAllCoreThreads();
        return exec;
    }
}
