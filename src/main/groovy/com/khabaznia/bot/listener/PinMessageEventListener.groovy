package com.khabaznia.bot.listener

import com.khabaznia.bot.event.PinMessageEvent
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.PinMessage
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.MessageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.PRIVATE_CHAT_TYPE

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
        def pinRequest = message.chat.type == PRIVATE_CHAT_TYPE
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
                .key(message.getText().bold())
                .messageId(Integer.valueOf(messageId))
    }

    private PinMessage getPinMessage(String messageId) {
        log.trace 'Pinning message with id {}', messageId
        context.getBean('pinMessage').messageId(messageId)
    }
}
