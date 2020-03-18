package com.dreamfoxick.telegrambot.services.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CallbackData {
    NEXT("next_"),
    BACK("back_");

    @Getter
    private final String callback;
}
