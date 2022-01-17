package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.MessageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ExecuteMethodsListener {

    Long codeForMessage
    @Autowired
    ApiMethodService apiMethodService
    @Autowired
    MessageService messageService

    @EventListener
    void onApplicationEvent(final ExecuteMethodsEvent event) {
        log.debug 'Sending {} requests', event.requests.size()
        event.requests
                .sort { it.order }
                .each {generateNewCode()}
                .collect { apiMethodService.execute(it, codeForMessage) }
                .each { apiMethodService.processResponse(it) }
                .findAll { it != null }
                .findAll {it instanceof MessageResponse}
                .findAll { it.result?.type != MessageType.SKIP }
                .each {messageService.saveMessage(it.result, codeForMessage)}
    }

    void generateNewCode(){
        codeForMessage = messageService.getUniqueCode()
    }
}
