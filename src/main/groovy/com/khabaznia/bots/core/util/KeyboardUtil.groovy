package com.khabaznia.bots.core.util

import com.khabaznia.bots.core.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton

import static com.khabaznia.bots.common.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU

class KeyboardUtil {

    static InlineButton fillButton(InlineButton inlineButton, String text, ConfirmationFlowDto confirmationFlowDto, String emoji = null) {
        inlineButton.callbackData(CONFIRMATION_MENU)
                .params(confirmationFlowDto.getClass()
                        .declaredFields
                        .findAll { !it.synthetic }
                        .findAll { it.name != "params" }
                        .findAll { it.name != 'menuTextBinding' }
                        .collectEntries { field ->
                            [field.name, confirmationFlowDto."$field.name"]
                        } << confirmationFlowDto.params << confirmationFlowDto.menuTextBinding)
                .text(text)
                .emoji(emoji)
        inlineButton
    }
}
