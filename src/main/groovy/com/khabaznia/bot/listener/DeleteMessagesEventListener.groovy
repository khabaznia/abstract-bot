package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Slf4j
@Component
class DeleteMessagesEventListener {

    @Autowired
    private BotRequestService requestService
    @Autowired
    private ApplicationContext context
    @Autowired
    private MessageService messageService

    @Async
    @EventListener
    void onApplicationEvent(DeleteMessagesEvent event) {
        def messageTypes = event.type ? [event.type] : [MessageType.DELETE, MessageType.EDIT_AND_DELETE]
        def currentChatCode = SessionUtil.currentChat.code
        def deleteMessageRequests = messageTypes
                .collect { messageService.getMessagesForTypeAndChat(it, currentChatCode) }
                .flatten()
                .collect { it as Message }
                .findAll { it.messageId != null && it.messageId != 0 }
                .collect { context.getBean('deleteMessage').messageId(it.messageId) }
                .each { requestService.execute(it) }
        log.info 'Delete {} messages from chat {}', deleteMessageRequests?.size(), currentChatCode
        messageTypes.each { messageService.removeMessagesOfType(it) }
    }
}
