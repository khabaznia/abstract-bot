package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.dto.ConfirmationFlowDto
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_ACTION
import static com.khabaznia.bot.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU
import static com.khabaznia.bot.meta.Emoji.*

@Slf4j
@Component
@BotController
class ConfirmationController extends AbstractBotController {

    @BotRequest(path = CONFIRMATION_ACTION, rawParams = true)
    String confirmationAction(Map<String, String> params) {
        log.debug 'Processing confirmation action. Next path - {}', params.nextPath
        params.nextPath
    }

    @BotRequest(path = CONFIRMATION_MENU, rawParams = true)
    confirmationMenu(Map<String, String> params) {
        log.debug 'Creating confirmation flow menu'
        def confirmationFlowDto = getConfirmationFlowDto(params)
        sendMessage
                .text(confirmationFlowDto.menuText ?: 'text.are.you.sure')
                .binding(confirmationFlowDto.menuTextBinding)
                .keyboard(getConfirmationKeyboard(confirmationFlowDto))
                .delete()
    }

    private ConfirmationFlowDto getConfirmationFlowDto(Map<String, String> params) {
        def dto = confirmationFlowDto
        params.findAll { dto.hasProperty(it.key) }
                .each { dto.setProperty(it.key, (it.value == 'null' ? null : it.value)) }
        dto.params(params.findAll { !dto.hasProperty(it.key) })
        dto.menuTextBinding(params.findAll { !dto.hasProperty(it.key) })
    }

    private InlineKeyboard getConfirmationKeyboard(ConfirmationFlowDto confirmationFlowDto) {
        def keyboard = inlineKeyboard
        keyboard.button(confirmationFlowDto.declinePathMessage ?: 'button.no', CONFIRMATION_ACTION,
                confirmationFlowDto.params << [nextPath: confirmationFlowDto.declinePath])
        keyboard.button(confirmationFlowDto.acceptPathMessage ?: 'button.yes', THUMB_UP, CONFIRMATION_ACTION,
                confirmationFlowDto.params << [nextPath: confirmationFlowDto.acceptPath])
        if (confirmationFlowDto.backPath) {
            keyboard.row()
            keyboard.button(confirmationFlowDto.backPathMessage ?: 'button.back', LEFT_ARROW, CONFIRMATION_ACTION,
                    confirmationFlowDto.params << [nextPath: confirmationFlowDto.backPath])
        }
        keyboard
    }
}
