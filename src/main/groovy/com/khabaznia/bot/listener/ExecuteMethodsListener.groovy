package com.khabaznia.bot.listener

import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.strategy.RequestProcessingStrategyContainer
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Slf4j
@Component
class ExecuteMethodsListener {

    @Autowired
    RequestProcessingStrategyContainer strategyContainer

    @EventListener
    void onApplicationEvent(ExecuteMethodsEvent event) {
        log.debug 'Processing {} requests', event.requests.size()
        event.requests
                .sort { it.order }
                .each { executeApiMethod(it) }
    }

    @Transactional
    void executeApiMethod(BaseRequest it) {
        def strategy = strategyContainer.getStrategyForRequest(it)
        def message = strategy.beforeProcess(it)
        def response = strategy.process(it)
        if (response)
            strategy.afterProcess(response, message)
    }
}
