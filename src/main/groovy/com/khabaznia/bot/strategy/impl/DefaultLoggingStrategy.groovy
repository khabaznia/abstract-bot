package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.LOG_INFO

@Slf4j
@Component(value = 'defaultLoggingStrategy')
class DefaultLoggingStrategy extends LoggingStrategy {

    @Override
    List<SendMessage> getRequestsForEvent(LogEvent event) {
        super.getRequestsForEvent(event)
    }

    @Override
    String getLogEmoji() {
        LOG_INFO
    }
}
