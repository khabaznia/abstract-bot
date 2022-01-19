package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.MESSAGE_CODE

@Slf4j
@Component(value = 'oneTimeInlineKeyboardRequestProcessingStrategy')
class OneTimeInlineKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Autowired
    MessageService messageService

    @Override
    Message beforeProcess(BaseRequest request) {
        def emptyMessage = messageService.getEmptyMessage()
        if (request instanceof AbstractKeyboardMessage && request.keyboard instanceof InlineKeyboard) {
            (request.keyboard as InlineKeyboard).addKeyboardParam(MESSAGE_CODE, emptyMessage.code.toString())
        }
        emptyMessage
    }

    @Override
    void afterProcess(BaseResponse response, Message message) {
        if (response instanceof MessageResponse) {
            populateMessage(message, response.result)
            log.debug 'Saving message with messageId: {}', response.result.messageId
            messageService.saveMessage(message)
        }
    }

    private static void populateMessage(Message target, Message source) {
        log.trace 'Before populating: {}', target
        source.metaPropertyValues
                .findAll { !['class', 'code'].contains(it.name) }
                .each {
                    target.setProperty(it.name, source.getProperty(it.name))
                }
        log.trace 'After populating: {}', target
    }

}
