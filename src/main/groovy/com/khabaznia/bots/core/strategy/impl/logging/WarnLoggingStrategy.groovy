package com.khabaznia.bots.core.strategy.impl.logging

import com.khabaznia.bots.core.event.LogEvent
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import com.khabaznia.bots.core.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.Emoji.LOG_WARNING
import static com.khabaznia.bots.core.routing.Constants.DUPLICATE_WARN_TO_ADMIN

@Slf4j
@Component(value = 'warnLoggingStrategy')
class WarnLoggingStrategy extends LoggingStrategy {

    @Override
    List<SendMessage> getRequestsForEvent(LogEvent event) {
        log.trace 'Warn logging. Duplicate to admin: {}', isEnabled(DUPLICATE_WARN_TO_ADMIN)
        def logRequests = super.getRequestsForEvent(event)
        def duplicateRequests = shouldDuplicateLog(logRequests) ? duplicateToAdminRequests(event) : []
        [logRequests, duplicateRequests].flatten() as List<SendMessage>
    }

    private boolean shouldDuplicateLog(List<SendMessage> logRequests) {
        (logRequests && isEnabled(DUPLICATE_WARN_TO_ADMIN)) || (logRequests.isEmpty())
    }

    @Override
    String getLogEmoji() {
        LOG_WARNING
    }

    private List<SendMessage> duplicateToAdminRequests(LogEvent event) {
        [getLogMessageRequest(event)]
                .each { it.chatId = adminChat?.code }
                .find { !it.chatId?.isEmpty() }
                .collect()
    }

}
