package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.event.PinMessageEvent
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import com.khabaznia.bots.core.service.MessageService
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
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
