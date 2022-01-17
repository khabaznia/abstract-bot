package com.khabaznia.bot.listener

import com.khabaznia.bot.event.SendChatActionEvent
import com.khabaznia.bot.service.ApiMethodService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SendChatActionListener {

    @Autowired
    ApplicationContext context
    @Autowired
    ApiMethodService apiMethodService

    @EventListener
    void onApplicationEvent(SendChatActionEvent event) {
        log.trace 'Sending chat action {}', event.actionType
        apiMethodService.execute(context.getBean('sendChatAction').action(event.actionType))
    }
}
