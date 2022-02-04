package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel


@Slf4j
@Component(value = 'editMessageRequestProcessingStrategy')
class EditMessageRequestProcessingStrategy extends RequestProcessingStrategy<EditMessage, BaseResponse> {

    @Autowired
    private ApplicationContext context

    void prepare(EditMessage request) {
        def messageToEdit = messageService.getMessage(request.label ?: request.messageId.toString())
        if (messageToEdit) {
            log.debug "Try to edit message with: uid {}, label {}, messageId {}", messageToEdit.uid, messageToEdit.label, messageToEdit.messageId
            populate(request, messageToEdit)
            request.setRelatedMessageUid(messageToEdit?.uid)
        }
    }

    private static void populate(EditMessage source, Message target) {
        source.setMessageId(source.getMessageId() ?: target.getMessageId())
        source.setLabel(source.getLabel() ?: target.getLabel())

        target.setKeyboard(toKeyboardModel(source.keyboard))
        target.setChat(SessionUtil.currentChat)
        target.setType(source.type)
    }
}
