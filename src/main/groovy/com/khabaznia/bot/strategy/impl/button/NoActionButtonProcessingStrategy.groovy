package com.khabaznia.bot.strategy.impl.button

import com.khabaznia.bot.model.Button
import com.khabaznia.bot.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'noActionButtonProcessingStrategy')
class NoActionButtonProcessingStrategy extends ButtonProcessingStrategy {

    @Override
    void processOnClick(Button button) {
        log.debug "No action for button"
    }
}
