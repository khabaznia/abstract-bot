package com.khabaznia.bots.core.listener

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.event.DeleteMessagesEvent
import com.khabaznia.bots.core.meta.request.impl.DeleteMessage
import com.khabaznia.bots.core.model.Message
import com.khabaznia.bots.core.service.BotRequestService
import com.khabaznia.bots.core.service.MessageService
import com.khabaznia.bots.core.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.enums.MessageFeature.DELETE

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
        def messageTypes = event.types ?: [DELETE]
        def currentChatCode = SessionUtil.currentChat.code
        def messagesToDelete = getMessagesToDelete(messageTypes, currentChatCode, event.updateId)
        messagesToDelete.collect { convertToRequest(it) }
                .each { requestService.execute(it) }
        messagesToDelete.each { messageService.removeMessageForUid(it.getUid()) }
        log.info 'Delete {} messages of type {} from chat {}', messagesToDelete?.size(), messageTypes, currentChatCode
    }

    private List<Message> getMessagesToDelete(List<MessageFeature> messageTypes, String currentChatCode, Integer updateId) {
        messageTypes.collect { messageService.getMessagesForTypeExcludingUpdateId(it, currentChatCode, updateId) }
                .flatten().collect { it as Message }
                .findAll { it.messageId != null && it.messageId != 0 }
    }

    private DeleteMessage convertToRequest(Message it) {
        context.getBean('deleteMessage').messageId(it.messageId)
    }
}
