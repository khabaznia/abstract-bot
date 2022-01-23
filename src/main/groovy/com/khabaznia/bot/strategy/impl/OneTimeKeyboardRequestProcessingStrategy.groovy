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
import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.ONE_TIME_KEYBOARD
import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'oneTimeKeyboardRequestProcessingStrategy')
class OneTimeKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Override
    Message beforeProcess(BaseRequest request) {
        Message message = getMessageFromRequest(request)
        log.trace(message as String)
        def result = messageService.saveMessage(message)
        log.trace 'Saved message without keyboard -> {}', result
        if (request instanceof AbstractKeyboardMessage && request.keyboard instanceof InlineKeyboard) {
            (request.keyboard as InlineKeyboard).addKeyboardParam(MESSAGE_CODE, message.code.toString())
            (request.keyboard as InlineKeyboard).addKeyboardParam(ONE_TIME_KEYBOARD, 'true')
            result.setKeyboard(messageService.saveKeyboard(toKeyboardModel(request.keyboard)))
            result = messageService.saveMessage(message)
            log.trace 'Saved message with keyboard -> {}', result
        }
        return result
    }
}
