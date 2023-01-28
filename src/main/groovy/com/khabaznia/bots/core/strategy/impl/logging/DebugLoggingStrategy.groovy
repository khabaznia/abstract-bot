package com.khabaznia.bots.core.strategy.impl.logging

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.event.LogEvent
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import com.khabaznia.bots.core.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.Emoji.LOG_DEBUG
import static com.khabaznia.bots.core.routing.Constants.DEBUG_LOGGING
import static com.khabaznia.bots.core.util.BotSession.currentChat

@Slf4j
@Component(value = 'debugLoggingStrategy')
class DebugLoggingStrategy extends LoggingStrategy {

    private List<ChatRole> excludedChatRoles = [ChatRole.LOGGING_CHAT]

    @Override
    List<SendMessage> getRequestsForEvent(LogEvent event) {
        isEnabled(DEBUG_LOGGING) && !excludedChatRoles.contains(currentChat.role)
                ? super.getRequestsForEvent(event)
                : []
    }

    @Override
    String getLogEmoji() {
        LOG_DEBUG
    }
}
