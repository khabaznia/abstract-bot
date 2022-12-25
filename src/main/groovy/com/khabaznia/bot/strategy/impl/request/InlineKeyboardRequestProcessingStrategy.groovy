package com.khabaznia.bot.strategy.impl.request

import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.MESSAGE_UID
import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'inlineKeyboardRequestProcessingStrategy')
class InlineKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<AbstractKeyboardMessage, BaseResponse> {

    @Override
    void prepare(AbstractKeyboardMessage request) {
        def message = getMessageFromRequest(request)
        if (request.keyboard instanceof InlineKeyboard) {
            (request.keyboard as InlineKeyboard).addKeyboardParam(MESSAGE_UID, message.uid)
            message.setKeyboard(toKeyboardModel(request.keyboard))
        }
        log.debug 'Saving message {} with inline keyboard', message.uid
        messageService.saveMessage(message)
    }
}
