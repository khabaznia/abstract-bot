package com.khabaznia.bot.strategy.impl


import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.DUPLICATE_WARN_TO_ADMIN
import static com.khabaznia.bot.meta.Emoji.LOG_WARNING

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
                .each { it.chatId = adminChat }
                .collect()
    }

}
