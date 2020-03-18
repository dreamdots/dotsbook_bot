package com.dreamfoxick.telegrambot.configuration.scheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Configuration
public class SchedulingConfiguration /*implements SchedulingConfigurer */ {

/*    @Bean
    public Executor schedulerExec() {
        val exec = new ScheduledThreadPoolExecutor(
                // целевой размер пула
                2,
                // кастомная фабрика потоков для именования и ведения статистики
                new CustomThreadFactory(
                        "scheduler",
                        "worker",
                        new AtomicInteger(0),
                        new AtomicInteger(0),
                        "Register scheduling thread: %s. Alive thread: %d",
                        "Exiting scheduling thread: %s. Alive thread: %d"),
                // при переполнении очереди задач самый старый элемент отбрасывается
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        logExecutorCharacters(exec, "schedulerExec");
        return exec;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(this.schedulerExec());
    }*/
}
