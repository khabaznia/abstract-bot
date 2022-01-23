package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.event.PinMessageEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'pinnedRequestProcessingStrategy')
class PinnedMessageRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Autowired
    MessageService messageService
    @Autowired
    ApplicationEventPublisher publisher

    @Override
    void afterProcess(BaseResponse response, Message message) {
        if (response instanceof MessageResponse && SessionUtil.currentChat.type != 'private') {
            super.afterProcess(response, message)
            log.trace 'Send pin event'
            publisher.publishEvent new PinMessageEvent(messageId: response.result.messageId)
        }
    }
}
