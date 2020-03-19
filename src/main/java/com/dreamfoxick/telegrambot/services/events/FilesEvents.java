package com.dreamfoxick.telegrambot.services.events;

import com.dreamfoxick.telegrambot.utils.LoggerUtils;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

@Component
@PropertySource("classpath:bot.properties")
public class FilesEvents {
    @Value("${temp_files_directory}")
    private String TEMP_FILES_DIRECTORY;

    private Path TEMP_DIRECTORY;

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void clean() {
        val dir = new File("./tempfiles");
        if (dir.isDirectory()) {
            stream(requireNonNull(dir.listFiles()))
                    .filter(File::isFile)
                    .forEach(File::delete);
        }
    }

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        TEMP_DIRECTORY = Paths.get(TEMP_FILES_DIRECTORY);
        if (Files.notExists(TEMP_DIRECTORY)) {
            try {
                Files.createDirectory(TEMP_DIRECTORY);
            } catch (IOException ex) {
                LoggerUtils.logStackTrace(ex);
            }
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void destroy() {
        if (Files.exists(TEMP_DIRECTORY)) {
            try {
                Files.delete(TEMP_DIRECTORY);
            } catch (IOException ex) {
                LoggerUtils.logStackTrace(ex);
            }
        }
    }
}
