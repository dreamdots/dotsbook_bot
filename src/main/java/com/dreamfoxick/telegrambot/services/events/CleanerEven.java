package com.dreamfoxick.telegrambot.services.events;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class CleanerEven {

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void clean() {
        val dir = new File("./tempfiles");

        if (dir.isDirectory()) {
            stream(requireNonNull(dir.listFiles()))
                    .filter(File::isFile)
                    .forEach(File::delete);
        }

        log.info(format("Directory: %s successfully cleaned", dir.getCanonicalPath()));
    }
}
