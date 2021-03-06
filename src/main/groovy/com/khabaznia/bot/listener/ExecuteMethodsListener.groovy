package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ExecuteMethodsListener {

    @Autowired
    private Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private ApplicationEventPublisher publisher
    @Autowired
    private BotRequestService requestService

    @EventListener
    void onApplicationEvent(ExecuteMethodsEvent event) {
        log.info 'Processing {} requests', event.requests.size()
        event.requests.each {
            requestProcessingStrategyMap.get(it.type).prepare(it)
            log.trace 'After prepare: {}', it
            requestService.execute(it)
        }
    }
}
