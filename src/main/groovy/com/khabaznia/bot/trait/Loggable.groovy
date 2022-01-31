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

    void sendLog(String text) {
        publisher.publishEvent(new LogEvent(text: text))
    }

    void sendWarnLog(String text) {
        publisher.publishEvent(new LogEvent(text: text, logType: LogType.WARN))
    }

    void sendLog(LogEvent event) {
        publisher.publishEvent(event)
    }

    void sendLogToAdmin(String text) {
        publisher.publishEvent(new LogEvent(text: text, logChat: LoggingChat.ADMIN))
    }

}