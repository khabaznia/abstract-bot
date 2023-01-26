package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_ACTION
import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU

@Slf4j
@Component
@BotController
class ConfirmationController extends AbstractBotController {

    @Autowired
    private FlowConversionUtil flowConversionUtil

    @BotRequest(path = CONFIRMATION_MENU, rawParams = true)
    confirmationMenu(Map<String, String> params) {
        log.debug 'Creating confirmation flow menu'
        def confirmationFlowDto = flowConversionUtil.getConfirmationFlowDto(params)
        sendMessage
                .text(confirmationFlowDto.menuText ?: 'text.are.you.sure')
                .binding(confirmationFlowDto.menuTextBinding)
                .keyboard(flowConversionUtil.getConfirmationKeyboard(confirmationFlowDto))
                .delete()
    }

    @BotRequest(path = CONFIRMATION_ACTION, rawParams = true)
    String confirmationAction(Map<String, String> params) {
        log.debug 'Processing confirmation action. Next path - {}', params.nextPath
        params.nextPath
    }
}
