package com.khabaznia.bot.listener

import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.UpdateKeyboardEvent
import com.khabaznia.bot.meta.request.impl.EditMessageKeyboard
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.fromKeyboardModel

@Slf4j
@Component
class UpdateKeyboardEventListener {

    @Autowired
    BotRequestService methodExecutionService
    @Autowired
    ApplicationContext context
    @Autowired
    MessageService messageService
    @Autowired
    Map<ButtonType, ButtonProcessingStrategy> buttonProcessingStrategyMap

    @EventListener
    void onApplicationEvent(UpdateKeyboardEvent event) {
        def messageWithKeyboard = messageService.getMessage(event.messageUid)
        if (messageWithKeyboard?.type == MessageType.INLINE_KEYBOARD) {
            processButton(event)
            log.trace 'Message to edit -> {}', messageWithKeyboard
            def editMessageRequest = toEditKeyboardRequest(messageWithKeyboard)
            log.info 'Editing keyboard from messageId {} from chat {}', messageWithKeyboard?.messageId, messageWithKeyboard.chat.code
            methodExecutionService.execute(editMessageRequest)
        }
    }

    private void processButton(UpdateKeyboardEvent event) {
        def button = messageService.getButton(event.buttonId)
        buttonProcessingStrategyMap.get(button.type).processOnClick(button)
    }

    private EditMessageKeyboard toEditKeyboardRequest(Message messageWithKeyboard) {
        def editMessageRequest = context.getBean('editMessageKeyboard')
        editMessageRequest.messageId(messageWithKeyboard.messageId)
        editMessageRequest.setKeyboard(fromKeyboardModel(messageService.getKeyboard(messageWithKeyboard.keyboard.id)))
        editMessageRequest
    }
}
