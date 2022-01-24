package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.MESSAGE_CODE
import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'inlineKeyboardRequestProcessingStrategy')
class InlineKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Override
    Message beforeProcess(BaseRequest request) {
        def message = messageService.saveMessage(getMessageFromRequest(request))
        if (request instanceof AbstractKeyboardMessage && request.keyboard instanceof InlineKeyboard) {
            (request.keyboard as InlineKeyboard).addKeyboardParam(MESSAGE_CODE, message.code.toString())
            message.setKeyboard(toKeyboardModel(request.keyboard))
        }
        log.trace 'Saved message with keyboard -> {}', message
        messageService.saveMessage(message)
    }
}
