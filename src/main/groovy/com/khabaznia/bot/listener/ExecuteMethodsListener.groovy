package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Slf4j
@Component
class ExecuteMethodsListener {

    @Autowired
    Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    ApplicationEventPublisher publisher
    @Autowired
    BotRequestService requestService

    @Async
    @EventListener
    void onApplicationEvent(ExecuteMethodsEvent event) {
        log.debug 'Processing {} requests', event.requests.size()

        event.requests.sort { it.order }
                .each { executeApiMethod(it) }
    }

    void executeApiMethod(BaseRequest request) {
        log.trace request as String
        requestProcessingStrategyMap.get(request.type).prepare(request)
        requestService.executeInQueue(request)
    }
}
