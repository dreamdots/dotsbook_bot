package com.dreamfoxick.telegrambot.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class LoggerUtils {

    public void logStackTrace(Exception ex) {
        log.error(String.format("%s: \n\n%s\n%s\n",
                ((ex.getMessage() == null) ? "Exception:" : ex.getMessage()),
                ((ex.getCause() == null) ? "->" : ex.getCause().toString()),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"))));
    }
}
