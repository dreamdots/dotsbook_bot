package com.dreamfoxick.telegrambot.services.statecontroller.impl;

import com.dreamfoxick.telegrambot.services.statecontroller.StateController;

import java.util.concurrent.ConcurrentHashMap;

public class StateControllerImpl<R, T> implements StateController<R, T> {
    private final ConcurrentHashMap<R, T> controllerMap = new ConcurrentHashMap<>();

    @Override
    public void addIfAbsent(R key,
                            T value) {
        controllerMap.putIfAbsent(key, value);
    }

    @Override
    public void updateIfPresent(R key,
                                T value) {
        controllerMap.computeIfPresent(key, (k, v) -> value);
    }

    @Override
    public T get(R key) {
        return controllerMap.get(key);
    }

    @Override
    public void remove(R key) {
        controllerMap.remove(key);
    }
}
