package com.khabaznia.bots.core.strategy

import com.khabaznia.bots.core.enums.ButtonType
import com.khabaznia.bots.core.enums.LogType
import com.khabaznia.bots.core.enums.MessageFeature
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Slf4j
@Configuration
class StrategiesContainer {

    @Autowired
    private ApplicationContext context

    @Bean(name = 'requestProcessingStrategyMap')
    Map<MessageFeature, RequestProcessingStrategy> requestProcessingStrategyMap() {
        Map<MessageFeature, RequestProcessingStrategy> map = [:]
        map[MessageFeature.PERSIST] = context.getBean('saveMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.DELETE] = context.getBean('saveMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.INLINE_KEYBOARD] = context.getBean('inlineKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.ONE_TIME_INLINE_KEYBOARD] = context.getBean('oneTimeKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.PINNED] = context.getBean('pinnedRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.REPLY_KEYBOARD] = context.getBean('replyKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.EDIT] = context.getBean('editMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageFeature.MEDIA] = context.getBean('mediaRequestProcessingStrategy') as RequestProcessingStrategy
        log.debug 'Request strategies map: {}', map
        map
    }

    @Bean(name = 'buttonProcessingStrategyMap')
    Map<ButtonType, ButtonProcessingStrategy> buttonProcessingStrategyMap() {
        Map<ButtonType, ButtonProcessingStrategy> map = [:]
        map[ButtonType.SIMPLE] = context.getBean('noActionButtonProcessingStrategy') as ButtonProcessingStrategy
        map[ButtonType.ONE_TIME] = context.getBean('oneTimeButtonProcessingStrategy') as ButtonProcessingStrategy
        map[ButtonType.SWITCH] = context.getBean('switchButtonProcessingStrategy') as ButtonProcessingStrategy
        log.debug 'Button strategies map: {}', map
        map
    }

    @Bean(name = 'loggingStrategyMap')
    Map<LogType, LoggingStrategy> loggingStrategyMap() {
        Map<LogType, LoggingStrategy> map = [:]
        map[LogType.DEBUG] = context.getBean('debugLoggingStrategy') as LoggingStrategy
        map[LogType.INFO] = context.getBean('defaultLoggingStrategy') as LoggingStrategy
        map[LogType.WARN] = context.getBean('warnLoggingStrategy') as LoggingStrategy
        log.debug 'Logging strategies map: {}', map
        map
    }
}
