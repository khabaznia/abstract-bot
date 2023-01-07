package com.khabaznia.bots.core.configuration.sheduling

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.task.TaskSchedulerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import java.util.concurrent.Executor

@Configuration
@EnableTransactionManagement
@EnableScheduling
class SchedulingConfig implements WebMvcConfigurer, TaskSchedulerCustomizer {

    @Autowired
    protected SessionFactory sessionFactory
    @Autowired
    private SchedulingExceptionHandler exceptionHandler

    @Bean(name = "applicationEventMulticaster")
    ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        new SimpleApplicationEventMulticaster(taskExecutor: new SyncTaskExecutor())
    }

    @Bean(name = "taskExecutor")
    Executor threadPoolTaskExecutor() {
        new SyncTaskExecutor()
    }

    @Override
    void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setPoolSize(10)
        taskScheduler.setThreadNamePrefix('bot_scheduled_job')
        taskScheduler.setErrorHandler(exceptionHandler)
    }
}