package com.khabaznia.bot.configuration.sheduling

import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import org.springframework.util.ErrorHandler

import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bot.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException

@Slf4j
@Component
class SchedulingExceptionHandler implements ErrorHandler, Loggable {

    @Override
    void handleError(Throwable throwable) {
        throwable.printStackTrace()
        def message = throwable instanceof UndeclaredThrowableException
                ? getMessageFromUndeclaredThrowableException(throwable)
                : throwable.message
        log.error 'Got scheduling exception -> {} : {}', throwable.class.name, message
        sendLog(new LogEvent(text: "Exception in sheduling: $message", logType: LogType.WARN, skipMetaInfo: true))
    }
}
