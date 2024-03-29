package com.khabaznia.bots.core.strategy.impl.button

import com.khabaznia.bots.core.model.Button
import com.khabaznia.bots.core.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'oneTimeButtonProcessingStrategy')
class OneTimeButtonProcessingStrategy extends ButtonProcessingStrategy {

    @Override
    void processOnClick(Button button) {
        log.debug "Deleting one-time button with id {}", button.id
        messageService.removeButton(button)
    }
}
