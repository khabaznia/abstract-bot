package com.khabaznia.example.job

import com.khabaznia.bot.scheduled.AbstractJob
import com.khabaznia.example.service.ExampleMessagesService
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'exampleJob')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ExampleJob extends AbstractJob {

    public String chatToSend

    @Autowired
    private ExampleMessagesService exampleMessagesService

    @Override
    protected void executeInternal() {
        requestsContainer << exampleMessagesService.simpleJobMessage(chatToSend)
        super.executeInternal()
    }

    @Override
    protected String getJobTitle() {
        'EXAMPLE JOB'
    }
}
