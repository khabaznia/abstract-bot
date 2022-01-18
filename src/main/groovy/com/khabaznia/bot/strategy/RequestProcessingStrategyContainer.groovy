package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.request.BaseRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Slf4j
@Component
class RequestProcessingStrategyContainer {

    @Autowired
    ApplicationContext context

    private Map<String, String> messageTypeStrategyMap

    @PostConstruct
    initStrategyMap() {
        messageTypeStrategyMap = [:]
        messageTypeStrategyMap.(MessageType.SKIP) = 'noActionRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.PERSIST) = 'saveMessageRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.DELETE) = 'noActionRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.REPLY_KEYBOARD) = 'noActionRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.FORCE_DELETE_INLINE_KEYBOARD) = 'noActionRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.ONE_TIME_INLINE_KEYBOARD) = 'noActionRequestProcessingStrategy'
        messageTypeStrategyMap.(MessageType.PINNED) = 'noActionRequestProcessingStrategy'
        log.trace 'Strategies container: {}', messageTypeStrategyMap
    }

    RequestProcessingStrategy getStrategyForRequest(BaseRequest request) {
        def strategyName = messageTypeStrategyMap.get(request.type.toString())
        log.debug "Processing message with strategy -> {}", strategyName
        context.getBean(strategyName) as RequestProcessingStrategy
    }
}
