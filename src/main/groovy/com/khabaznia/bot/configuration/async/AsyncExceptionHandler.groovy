package com.khabaznia.bot.configuration.async

import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.stereotype.Component

import java.lang.reflect.Method
import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bot.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException

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
        sendWarnLog("Exception in async: $message")
    }
}
