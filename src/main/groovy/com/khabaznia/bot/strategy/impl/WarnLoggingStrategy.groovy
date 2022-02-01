package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.LOG_WARNING
import static com.khabaznia.bot.core.Constants.DUPLICATE_WARN_TO_ADMIN

@Slf4j
@Component(value = 'warnLoggingStrategy')
class WarnLoggingStrategy extends LoggingStrategy {

    @Override
    List<SendMessage> getRequestForEvent(LogEvent event) {
        log.trace 'Warn logging. Duplicate to admin: {}', isEnabled(DUPLICATE_WARN_TO_ADMIN)
        getChatId(event) == getConfig(LoggingChat.ADMIN.chatIdConfig)
                ? super.getRequestForEvent(event)
                : [super.getRequestForEvent(event), duplicateToAdminRequest(event)].flatten() as List<SendMessage>
    }

    @Override
    String getLogEmoji() {
        LOG_WARNING
    }

    private List<SendMessage> duplicateToAdminRequest(LogEvent event) {
        !isEnabled(DUPLICATE_WARN_TO_ADMIN) ? [] : super.getRequestForEvent(event)
                .each { it.chatId = getConfig(LoggingChat.ADMIN.chatIdConfig) }
                .collect()
    }

}
