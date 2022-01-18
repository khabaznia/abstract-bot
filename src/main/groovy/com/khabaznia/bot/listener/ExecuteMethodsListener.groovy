package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.RequestProcessingStrategyContainer
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ExecuteMethodsListener {

    @Autowired
    RequestProcessingStrategyContainer strategyContainer

    @EventListener
    void onApplicationEvent(final ExecuteMethodsEvent event) {
        log.debug 'Processing {} requests', event.requests.size()
        event.requests
                .sort { it.order }
                .each {
                    def strategy = strategyContainer.getStrategyForRequest(it)
                    strategy.beforeProcess(it)
                    def response = strategy.process(it)
                    strategy.afterProcess(response)
                }
    }
}
