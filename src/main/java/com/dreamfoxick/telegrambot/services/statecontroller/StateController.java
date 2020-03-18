package com.dreamfoxick.telegrambot.services.statecontroller;

import com.dreamfoxick.telegrambot.utils.annotation.ReadyToUse;

@ReadyToUse
public interface StateController<R, T> {

    void addIfAbsent(R key,
                     T value);

    void updateIfPresent(R key,
                         T value);

    T get(R key);

    void remove(R key);
}
