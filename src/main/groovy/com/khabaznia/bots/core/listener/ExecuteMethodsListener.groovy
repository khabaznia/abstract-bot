package com.khabaznia.bots.core.listener

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.event.ExecuteMethodsEvent
import com.khabaznia.bots.core.service.BotRequestService
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ExecuteMethodsListener {

    @Autowired
    private Map<MessageFeature, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private ApplicationEventPublisher publisher
    @Autowired
    private BotRequestService requestService

    @EventListener
    void onApplicationEvent(ExecuteMethodsEvent event) {
        log.info 'Processing {} requests', event.requests.size()
        event.requests.findAll().each {
            it.features.each {feature ->
                requestProcessingStrategyMap.get(feature).prepare(it)
            }
            log.trace 'After prepare: {}', it
            requestService.execute(it)
        }
    }
}
