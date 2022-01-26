package com.khabaznia.bot.listener

import com.khabaznia.bot.event.PinMessageEvent
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.PinMessage
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class PinMessageEventListener {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ApiMethodService apiMethodService
    @Autowired
    private MessageService messageService

    @EventListener
    void onApplicationEvent(PinMessageEvent event) {
        log.trace 'Pin message with id {}', event.messageId
        apiMethodService.execute SessionUtil.currentChat.type == 'private'
                ? getEditMessage(event.messageId)
                : getPinMessage(event.messageId)
    }

    private EditMessage getEditMessage(String messageId) {
        log.trace 'Editing message with id {}', messageId
        def message = messageService.getMessageForMessageId(Integer.valueOf(messageId))
        def boldText = "<b>${message.getText()}</b>"
        message.setText(boldText)
        messageService.saveMessage(message)
        context.getBean('editMessage')
                .key(boldText)
                .messageId(Integer.valueOf(messageId))
    }

    private PinMessage getPinMessage(String messageId) {
        log.trace 'Pinning message with id {}', messageId
        context.getBean('pinMessage').messageId(messageId)
    }
}
