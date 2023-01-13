package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_ACTION
import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU
import static com.khabaznia.bots.core.meta.Emoji.LEFT_ARROW
import static com.khabaznia.bots.core.meta.Emoji.THUMB_UP

@Slf4j
@Component
@BotController
class ConfirmationController extends AbstractFlowController {

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
        def dto = fillDto(params, confirmationFlowDto)
        setParams(params, dto, 'params')
        setParams(params, dto, 'menuTextBinding')
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
