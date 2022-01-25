package com.khabaznia.bot.trait

import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
trait Logged {

    @Autowired
    private ApplicationEventPublisher publisher

    void botLog(String text) {
        publisher.publishEvent(new LogEvent(text: text))
    }

    void botLog(SendMessage request) {
        publisher.publishEvent(new LogEvent(request: request))
    }

    void botLog(String text, LogType type) {
        publisher.publishEvent(new LogEvent(text: text, logType: type))
    }

    void botLog(SendMessage request, LogType type) {
        publisher.publishEvent(new LogEvent(request: request, logType: type))
    }

    void adminLog(String text) {
        publisher.publishEvent(new LogEvent(text: text, logChat: LoggingChat.ADMIN))
    }

    void adminLog(SendMessage request) {
        publisher.publishEvent(new LogEvent(request: request, logChat: LoggingChat.ADMIN))
    }

    void adminLog(String text, LogType type) {
        publisher.publishEvent(new LogEvent(text: text, logType: type, logChat: LoggingChat.ADMIN))
    }

    void adminLog(SendMessage request, LogType type) {
        publisher.publishEvent(new LogEvent(request: request, logType: type, logChat: LoggingChat.ADMIN))
    }

    void log(LogEvent event) {
        publisher.publishEvent(event)
    }

    void log(String updateMessage){
        publisher.publishEvent(new LogEvent(text: updateMessage, logType: LogType.DEBUG))
    }
}