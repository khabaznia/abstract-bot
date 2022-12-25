package com.khabaznia.bot.trait

import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

@Slf4j
trait Loggable {

    @Autowired
    private ApplicationEventPublisher publisher

    void sendLog(String text, Map<String, String> binding = null) {
        publisher.publishEvent(new LogEvent(
                text: text,
                binding: binding))
    }

    void sendWarnLog(String text, Map<String, String> binding = null) {
        publisher.publishEvent(new LogEvent(
                text: text,
                binding: binding,
                logType: LogType.WARN))
    }

    void sendLog(LogEvent event) {
        publisher.publishEvent(event)
    }

    void sendLogToAdmin(String text, Map<String, String> binding = null, boolean skipMetaInfo = false) {
        publisher.publishEvent(new LogEvent(
                text: text,
                binding: binding,
                logChat: LoggingChat.ADMIN,
                skipMetaInfo: skipMetaInfo))
    }

}