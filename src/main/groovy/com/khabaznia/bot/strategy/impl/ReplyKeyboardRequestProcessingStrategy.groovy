package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'replyKeyboardRequestProcessingStrategy')
class ReplyKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Autowired
    ApplicationEventPublisher publisher

    @Override
    Message beforeProcess(BaseRequest request) {
        publisher.publishEvent new DeleteMessagesEvent(type: MessageType.REPLY_KEYBOARD)
        def message = messageService.saveMessage(getMessageFromRequest(request))
        if (request instanceof AbstractKeyboardMessage) {
            message.setKeyboard(toKeyboardModel(request.keyboard))
        }
        log.trace 'Saved message with reply keyboard -> {}', message
        messageService.saveMessage(message)
    }
}
