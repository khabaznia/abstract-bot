package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.BUTTON_PARAMETERS.MESSAGE_UID
import static com.khabaznia.bots.core.controller.Constants.BUTTON_PARAMETERS.ONE_TIME_KEYBOARD
import static com.khabaznia.bots.core.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'oneTimeKeyboardRequestProcessingStrategy')
class OneTimeKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<AbstractKeyboardMessage, BaseResponse> {

    @Override
    void prepare(AbstractKeyboardMessage request) {
        def message = getMessageFromRequest(request)
        if (request.keyboard instanceof InlineKeyboard) {
            (request.keyboard as InlineKeyboard).addKeyboardParam(MESSAGE_UID, message.uid)
            (request.keyboard as InlineKeyboard).addKeyboardParam(ONE_TIME_KEYBOARD, 'true')
            message.setKeyboard(toKeyboardModel(request.keyboard))
        }
        log.debug 'Saving message {} with one-time keyboard', message.uid
        messageService.saveMessage(message)
    }
}
