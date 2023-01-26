package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.meta.request.impl.EditMessage
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.model.Message
import com.khabaznia.bots.core.service.ChatService
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
@Component(value = 'editMessageRequestProcessingStrategy')
class EditMessageRequestProcessingStrategy extends RequestProcessingStrategy<EditMessage, BaseResponse> {

    @Autowired
    private ApplicationContext context
    @Autowired
    protected ChatService chatService

    void prepare(EditMessage request) {
        def messageToEdit = messageService.getMessage(request.label ?: request.messageId.toString())
        if (messageToEdit) {
            log.debug "Try to edit message with: uid {}, label {}, messageId {}", messageToEdit.uid, messageToEdit.label, messageToEdit.messageId
            populate(request, messageToEdit)
            request.setRelatedMessageUid(messageToEdit?.uid)
            messageService.saveMessage(messageToEdit)
        }
    }

    private void populate(EditMessage source, Message target) {
        source.setMessageId(source.getMessageId() ?: target.getMessageId())
        source.setLabel(source.getLabel() ?: target.getLabel())

        target.setKeyboard(toKeyboardModel(source.keyboard))
        target.setChat(chatService.getChatByCode(source.chatId))
        target.features.addAll(source.features)
    }
}
