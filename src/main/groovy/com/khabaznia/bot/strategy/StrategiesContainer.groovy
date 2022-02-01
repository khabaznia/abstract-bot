package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.MessageType
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
    Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap() {
        Map<MessageType, RequestProcessingStrategy> map = [:]
        map[MessageType.SKIP] = context.getBean('noActionRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.PERSIST] = context.getBean('saveMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.DELETE] = context.getBean('saveMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.INLINE_KEYBOARD] = context.getBean('inlineKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.ONE_TIME_INLINE_KEYBOARD] = context.getBean('oneTimeKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.PINNED] = context.getBean('pinnedRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.REPLY_KEYBOARD] = context.getBean('replyKeyboardRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.EDIT] = context.getBean('editMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.EDIT_AND_DELETE] = context.getBean('editMessageRequestProcessingStrategy') as RequestProcessingStrategy
        map[MessageType.MEDIA] = context.getBean('noActionRequestProcessingStrategy') as RequestProcessingStrategy
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
