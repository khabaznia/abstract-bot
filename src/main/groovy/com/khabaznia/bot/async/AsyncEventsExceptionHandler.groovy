package com.khabaznia.bot.async

import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.stereotype.Component

import java.lang.reflect.Method

@Slf4j
@Component
class AsyncEventsExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
        log.error 'While executing method {}, got exception -> {} : {}', method.name, throwable.class.name, throwable.message
    }
}
