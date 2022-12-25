package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.exception.BotLogEventException
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Slf4j
@Component
class LogListener {

    @Autowired
    private Map<LogType, LoggingStrategy> loggingStrategyMap
    @Autowired
    private BotRequestService requestService

    @Async
    @EventListener
    void onApplicationEvent(LogEvent event) {
        try {
            def strategy = loggingStrategyMap.get(event.logType)
            def requests = strategy.getRequestsForEvent(event)
                    .findAll { it.chatId }
            log.trace 'Log event: {}', requests[0]?.text
            requests.each { requestService.execute it }
        } catch (Exception ex) {
            throw new BotLogEventException(ex.message, ex.cause)
        }
    }
}
