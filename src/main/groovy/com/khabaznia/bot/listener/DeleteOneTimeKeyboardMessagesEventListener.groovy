package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.event.DeleteOneTimeKeyboardMessagesEvent
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.MessageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class DeleteOneTimeKeyboardMessagesEventListener {

    @Autowired
    private BotRequestService requestService
    @Autowired
    private ApplicationContext context
    @Autowired
    private MessageService messageService

    @EventListener
    void onApplicationEvent(DeleteOneTimeKeyboardMessagesEvent event) {
        def messageToEdit = messageService.getMessage(event.messageUid)
        if (messageToEdit?.features?.contains(MessageFeature.ONE_TIME_INLINE_KEYBOARD)) {
            log.trace 'Message with one-time keyboard -> {}', messageToEdit
            def editMessageRequest = context.getBean('editMessage')
            editMessageRequest.messageId(messageToEdit.messageId)
            editMessageRequest.setKeyboard(null)
            requestService.execute(editMessageRequest)
            log.info 'Delete one-time keyboard in message with id {} from chat {}', messageToEdit?.messageId, messageToEdit.chat.code
            messageService.removeMessageForUid(event.messageUid)
        }
    }
}
