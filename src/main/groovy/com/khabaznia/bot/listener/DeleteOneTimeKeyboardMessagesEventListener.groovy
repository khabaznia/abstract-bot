package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.DeleteOneTimeKeyboardMessagesEvent
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
class DeleteOneTimeKeyboardMessagesEventListener {

    @Autowired
    ApiMethodService methodExecutionService
    @Autowired
    ApplicationContext context
    @Autowired
    MessageService messageService

    @EventListener
    void onApplicationEvent(DeleteOneTimeKeyboardMessagesEvent event) {
        def messageToEdit = messageService.getMessageForCode(event.code)
        if (messageToEdit) {
            log.trace 'Message to edit -> {}', messageToEdit
            def editMessageRequest = context.getBean('editMessageKeyboard')
            editMessageRequest.messageId(messageToEdit.messageId)
            editMessageRequest.setKeyboard(null)
            methodExecutionService.execute(editMessageRequest)
            log.info 'Delete keyboard from messageId {} from chat {}', messageToEdit?.messageId, messageToEdit.chat.code
            messageService.removeMessageForCode(event.code)
        }
    }
}
