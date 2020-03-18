package com.dreamfoxick.telegrambot;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@EnableAsync
@SpringBootApplication
public class App {
    public static URI getStartUpLocation() throws URISyntaxException {
        val loc = App.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        log.info(String.format("Startup location: %s", loc.toString()));
        return loc;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
