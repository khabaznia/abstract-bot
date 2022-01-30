package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import io.micrometer.core.instrument.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class DeleteMessagesEventListener {

    @Autowired
    BotRequestService requestService
    @Autowired
    ApplicationContext context
    @Autowired
    MessageService messageService

    @EventListener
    void onApplicationEvent(DeleteMessagesEvent event) {
        def messageTypes = event.type ? [event.type] : [MessageType.DELETE, MessageType.EDIT_AND_DELETE]
        def currentChatCode = SessionUtil.currentChat.code
        def deleteMessageRequests = messageTypes
                .collect { messageService.getMessagesForTypeAndChat(it, currentChatCode) }
                .flatten()
                .collect { it as Message }
                .each { log.debug('====================== message to delete -> {} ', it.toString()) }
                .each { log.debug('====================== message to delete messageId -> {} ', it.messageId.toString()) }
                .each { log.debug('====================== message to delete -> messageCode {} ', it.uid.toString()) }
                .findAll { it.messageId != 0 }
                .collect { context.getBean('deleteMessage').messageId(it.messageId) }
                .each { requestService.executeInQueue(it) }
        log.info 'Delete {} messages from chat {}', deleteMessageRequests?.size(), currentChatCode
        messageTypes.each { messageService.removeMessagesOfType(it) }
    }
}
