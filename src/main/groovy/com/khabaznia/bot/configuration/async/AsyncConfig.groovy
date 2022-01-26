package com.khabaznia.bot.configuration.async

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import java.util.concurrent.Executor

@Configuration
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
class AsyncConfig implements WebMvcConfigurer {

    @Autowired
    SessionFactory sessionFactory

    @Bean(name = "applicationEventMulticaster")
    ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        new SimpleApplicationEventMulticaster(taskExecutor: new SyncTaskExecutor())
    }

    @Bean(name = "threadPoolTaskExecutor")
    Executor threadPoolTaskExecutor() {
        new SyncTaskExecutor()
    }
}