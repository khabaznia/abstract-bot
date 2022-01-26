package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.DEBUG_LOGGING
import static com.khabaznia.bot.meta.Emoji.LOG_DEBUG

@Slf4j
@Component(value = 'debugLoggingStrategy')
class DebugLoggingStrategy extends LoggingStrategy {

    @Override
    List<SendMessage> getRequestForEvent(LogEvent event) {
        log.trace 'Debug logging'
        isEnabled(DEBUG_LOGGING) ? super.getRequestForEvent(event) : []
    }

    @Override
    String getLogEmoji() {
        LOG_DEBUG
    }
}
