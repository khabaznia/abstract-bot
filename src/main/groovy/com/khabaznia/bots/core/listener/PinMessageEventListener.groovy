package com.khabaznia.bots.core.listener

import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.event.PinMessageEvent
import com.khabaznia.bots.core.meta.request.impl.EditMessage
import com.khabaznia.bots.core.meta.request.impl.PinMessage
import com.khabaznia.bots.core.model.Message
import com.khabaznia.bots.core.service.BotRequestService
import com.khabaznia.bots.core.service.MessageService
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
    private BotRequestService requestService
    @Autowired
    private MessageService messageService

    @EventListener
    void onApplicationEvent(PinMessageEvent event) {
        log.info 'Pin message with id {}', event.messageId
        def message = messageService.getMessage(event.messageId)
        def pinRequest = message.chat.type == ChatType.PRIVATE
                ? getEditMessage(event.messageId, message)
                : getPinMessage(event.messageId)
        pinRequest.setChatId(message.chat.code)
        requestService.execute(pinRequest)
    }

    private EditMessage getEditMessage(String messageId, Message message) {
        log.trace 'Editing (instead pinning) message with id {}', messageId
        message.setText(message.getText().bold())
        messageService.saveMessage(message)
        context.getBean('editMessage')
                .text(message.getText().bold())
                .messageId(Integer.valueOf(messageId))
    }

    private PinMessage getPinMessage(String messageId) {
        log.trace 'Pinning message with id {}', messageId
        context.getBean('pinMessage').messageId(messageId)
    }
}
