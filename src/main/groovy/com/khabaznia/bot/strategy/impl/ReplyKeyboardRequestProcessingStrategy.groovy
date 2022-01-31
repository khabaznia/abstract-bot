package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component


@Slf4j
@Component(value = 'replyKeyboardRequestProcessingStrategy')
class ReplyKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<AbstractKeyboardMessage, BaseResponse> {

    @Autowired
    private ApplicationEventPublisher publisher

    @Override
    void prepare(AbstractKeyboardMessage request) {
        log.debug 'Removing previous message with reply keyboard'
        messageService.removeMessagesOfType(MessageType.REPLY_KEYBOARD)
        super.prepare(request)
    }
}
