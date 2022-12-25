package com.khabaznia.bot.util

import com.khabaznia.bot.dto.ConfirmationFlowDto
import com.khabaznia.bot.meta.keyboard.impl.InlineButton

import static com.khabaznia.bot.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU

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
