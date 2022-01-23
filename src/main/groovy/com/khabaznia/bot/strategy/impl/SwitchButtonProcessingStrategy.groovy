package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.model.Button
import com.khabaznia.bot.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.CHECKED_MARK
import static com.khabaznia.bot.meta.Emoji.CROSS_MARK

@Slf4j
@Component(value = 'switchButtonProcessingStrategy')
class SwitchButtonProcessingStrategy extends ButtonProcessingStrategy{

    @Override
    void processOnClick(Button button) {
        log.trace 'Modifying switch button -> {}', button
        def enabled = button.params.get(ButtonType.SWITCH.paramKey) as Boolean
        button.params.put(ButtonType.SWITCH.paramKey, (!enabled) as String)
        button.setEmoji(!enabled ? CHECKED_MARK : CROSS_MARK)
        log.debug 'Save switch button: {}', button
        messageService.saveButton(button)
    }
}
