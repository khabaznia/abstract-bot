package com.khabaznia.bots.core.listener

import com.khabaznia.bots.core.event.SendChatActionEvent
import com.khabaznia.bots.core.service.BotRequestService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Slf4j
@Component
class SendChatActionListener {

    @Autowired
    private ApplicationContext context
    @Autowired
    private BotRequestService requestService

    @Async
    @EventListener
    void onApplicationEvent(SendChatActionEvent event) {
        if (event.actionType) {
            log.debug 'Sending chat action {}', event.actionType
            requestService.execute(context.getBean('sendChatAction').action(event.actionType))
        }
    }
}
