package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.meta.request.impl.DeleteMessage
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

import static com.khabaznia.bot.enums.MessageFeature.DELETE

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
