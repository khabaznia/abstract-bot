package com.khabaznia.bot.strategy.impl.logging

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.DEBUG_LOGGING
import static com.khabaznia.bot.meta.Emoji.LOG_DEBUG
import static com.khabaznia.bot.util.SessionUtil.currentChat

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
