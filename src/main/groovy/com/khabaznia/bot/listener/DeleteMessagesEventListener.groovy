package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class DeleteMessagesEventListener {

    @Autowired
    ApiMethodService methodExecutionService
    @Autowired
    ApplicationContext context
    @Autowired
    MessageService messageService

    @EventListener
    void onApplicationEvent(DeleteMessagesEvent event) {
        def messageType = event.type ?: MessageType.DELETE
        def currentChatCode = SessionUtil.currentChat.code
        def deleteMessageRequests = messageService.getMessagesForTypeAndChat(messageType, currentChatCode)
                .collect { context.getBean('deleteMessage').messageId(it.messageId) }
                .collect { methodExecutionService.execute(it) }
        log.info 'Delete {} messages from chat {}', deleteMessageRequests?.size(), currentChatCode
        messageService.removeMessagesOfType(messageType)
    }
}
