package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.event.PinMessageEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
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
    void processResponse(BaseResponse response) {
        if (response instanceof MessageResponse) {
            super.processResponse(response)
            log.debug 'Sending pin event for message {}', response.result.messageId
            publisher.publishEvent new PinMessageEvent(messageId: response.result.messageId)
        }
    }
}
