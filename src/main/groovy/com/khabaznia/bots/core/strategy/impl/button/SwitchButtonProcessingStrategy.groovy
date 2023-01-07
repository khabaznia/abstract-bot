package com.khabaznia.bots.core.strategy.impl.button

import com.khabaznia.bots.core.enums.ButtonType
import com.khabaznia.bots.core.model.Button
import com.khabaznia.bots.core.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.Emoji.CHECKED_MARK
import static com.khabaznia.bots.core.meta.Emoji.CROSS_MARK

@Slf4j
@Component(value = 'switchButtonProcessingStrategy')
class SwitchButtonProcessingStrategy extends ButtonProcessingStrategy {

    @Override
    void processOnClick(Button button) {
        def enabled = Boolean.valueOf(button.params.get(ButtonType.SWITCH.paramKey))
        log.trace 'Modifying switch button with id {}. Enabled -> {}', button.id, enabled
        button.params.put(ButtonType.SWITCH.paramKey, (!enabled) as String)
        button.setEmoji(!enabled ? CHECKED_MARK : CROSS_MARK)
        messageService.saveButton(button)
    }
}
