package com.khabaznia.bots.core.listener

import com.khabaznia.bots.core.enums.ButtonType
import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.event.UpdateKeyboardEvent
import com.khabaznia.bots.core.meta.request.impl.EditMessage
import com.khabaznia.bots.core.model.Message
import com.khabaznia.bots.core.service.BotRequestService
import com.khabaznia.bots.core.service.MessageService
import com.khabaznia.bots.core.strategy.ButtonProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.mapper.KeyboardMapper.fromKeyboardModel

@Slf4j
@Component
class UpdateKeyboardEventListener {

    @Autowired
    private BotRequestService requestService
    @Autowired
    private ApplicationContext context
    @Autowired
    private MessageService messageService
    @Autowired
    private Map<ButtonType, ButtonProcessingStrategy> buttonProcessingStrategyMap

    @EventListener
    void onApplicationEvent(UpdateKeyboardEvent event) {
        def messageWithKeyboard = messageService.getMessage(event.messageUid)
        if (messageWithKeyboard?.features?.contains(MessageFeature.INLINE_KEYBOARD)) {
            processButton(event)
            log.trace 'Message with keyboard to edit -> {}', messageWithKeyboard
            def editMessageRequest = toEditKeyboardRequest(messageWithKeyboard)
            log.info 'Editing keyboard from messageId {} from chat {}', messageWithKeyboard?.messageId, messageWithKeyboard.chat.code
            requestService.execute(editMessageRequest)
        }
    }

    private void processButton(UpdateKeyboardEvent event) {
        def button = messageService.getButton(event.buttonId)
        buttonProcessingStrategyMap.get(button.type).processOnClick(button)
    }

    private EditMessage toEditKeyboardRequest(Message messageWithKeyboard) {
        def editMessageRequest = context.getBean('editMessage')
        editMessageRequest.messageId(messageWithKeyboard.messageId)
        editMessageRequest.setKeyboard(fromKeyboardModel(messageService.getKeyboard(messageWithKeyboard.keyboard.id)))
        editMessageRequest
    }
}
