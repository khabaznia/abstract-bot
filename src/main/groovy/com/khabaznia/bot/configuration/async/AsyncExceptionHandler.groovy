package com.khabaznia.bot.configuration.async

import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.stereotype.Component

import java.lang.reflect.Method

@Slf4j
@Component
class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        log.error 'While executing method {}, got exception -> {} : {}', method.name, throwable.class.name, throwable.message
    }
}
