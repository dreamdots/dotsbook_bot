package com.dreamfoxick.telegrambot.services.statecontroller;

import com.dreamfoxick.telegrambot.services.statecontroller.impl.StateControllerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Map;

@Configuration
public class StateControllerConfiguration {

    @Bean
    public StateController<Long, State> stateController() {
        return new StateControllerImpl<>();
    }

    @Bean
    public StateController<String, String> downloadQueryController() {
        return new StateControllerImpl<>();
    }

    @Bean
    public StateController<String, Map<Integer, EditMessageText>> updateQueryController() {
        return new StateControllerImpl<>();
    }
}
