package com.khabaznia.bots.core.configuration.sheduling

import com.khabaznia.bots.core.enums.LogType
import com.khabaznia.bots.core.event.LogEvent
import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import org.springframework.util.ErrorHandler

import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bots.core.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException

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
