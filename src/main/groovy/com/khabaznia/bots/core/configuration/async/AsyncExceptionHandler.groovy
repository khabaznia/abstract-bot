package com.khabaznia.bots.core.configuration.async

import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.stereotype.Component

import java.lang.reflect.Method
import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bots.core.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException
import static com.khabaznia.bots.core.exception.ExceptionUtil.isLogEventException

@Slf4j
@Component
class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler, Loggable {

    @Override
    void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        throwable.printStackTrace()
        def message = throwable instanceof UndeclaredThrowableException
                ? getMessageFromUndeclaredThrowableException(throwable)
                : throwable.message
        log.error 'Got async exception in method {}, -> {} : {}', method.name, throwable.class.name, message
        tryToLogToChat(throwable, message)
    }

    private tryToLogToChat(Throwable throwable, String message) {
        try {
            sendWarnLog(isLogEventException(throwable)
                    ? 'Exception during sending log event. Please re-check in logs.'
                    : "Exception in async: $message")
        } catch (Exception ex) {
            log.error "Some issue during logging event. Minor - don't handle it"
            ex.printStackTrace()
        }
    }
}
