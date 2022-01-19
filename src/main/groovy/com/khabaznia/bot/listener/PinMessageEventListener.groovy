package com.khabaznia.bot.listener

import com.khabaznia.bot.event.PinMessageEvent
import com.khabaznia.bot.event.SendChatActionEvent
import com.khabaznia.bot.service.ApiMethodService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class PinMessageEventListener {

    @Autowired
    ApplicationContext context
    @Autowired
    ApiMethodService apiMethodService

    @EventListener
    void onApplicationEvent(PinMessageEvent event) {
        log.trace 'Pin message with id {}', event.messageId
        apiMethodService.execute(context.getBean('pinMessage').messageId(event.messageId))
    }
}
