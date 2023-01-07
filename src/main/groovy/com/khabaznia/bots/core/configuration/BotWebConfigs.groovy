package com.khabaznia.bots.core.configuration

import com.khabaznia.bots.core.routing.interceptor.UpdateChatDataInterceptor
import com.khabaznia.bots.core.routing.interceptor.UpdateUserDataInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class BotWebConfigs implements WebMvcConfigurer{

    @Autowired
    private UpdateUserDataInterceptor updateUserDataInterceptor
    @Autowired
    private UpdateChatDataInterceptor updateChatDataInterceptor

    @Override
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(updateUserDataInterceptor)
        registry.addInterceptor(updateChatDataInterceptor)
    }
}
