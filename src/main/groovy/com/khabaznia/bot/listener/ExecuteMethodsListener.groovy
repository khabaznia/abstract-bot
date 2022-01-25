package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

import javax.transaction.Transactional

import static com.khabaznia.bot.core.Constants.DELETE_PREVIOUS_INLINE_KEYBOARDS

@Slf4j
@Component
class ExecuteMethodsListener implements Configured {

    @Autowired
    Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    ApplicationEventPublisher publisher

    @Async
    @EventListener
    void onApplicationEvent(ExecuteMethodsEvent event) {
        log.debug 'Processing {} requests', event.requests.size()
        if (isEnabled(DELETE_PREVIOUS_INLINE_KEYBOARDS) && hasInlineKeyboard(event)) {
            publisher.publishEvent new DeleteMessagesEvent(type: MessageType.INLINE_KEYBOARD)
            publisher.publishEvent new DeleteMessagesEvent(type: MessageType.ONE_TIME_INLINE_KEYBOARD)
        }
        event.requests
                .sort { it.order }
                .each { executeApiMethod(it) }
    }

    private static Boolean hasInlineKeyboard(ExecuteMethodsEvent event) {
        event.requests.find {
            it.type == MessageType.ONE_TIME_INLINE_KEYBOARD || it.type == MessageType.INLINE_KEYBOARD
        } != null
    }

    @Transactional
    void executeApiMethod(BaseRequest request) {
        log.trace request as String
        def strategy = requestProcessingStrategyMap.get(request.type)
        def message = strategy.beforeProcess(request)
        def response = strategy.process(request)
        if (response)
            strategy.afterProcess(response, message)
    }
}
