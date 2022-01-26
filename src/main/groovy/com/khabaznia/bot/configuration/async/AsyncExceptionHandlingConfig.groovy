package com.khabaznia.bot.configuration.async

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurerSupport
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
class AsyncExceptionHandlingConfig extends AsyncConfigurerSupport {

    @Autowired
    private AsyncExceptionHandler asyncExceptionHandler

    @Override
    AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        asyncExceptionHandler
    }

}
