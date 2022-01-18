package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.impl.BooleanResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.util.SessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class ResponseMapper {

    @Autowired
    KeyboardMapper keyboardMapper

    BooleanResponse toResponse(Boolean result, MessageType type) {
        new BooleanResponse(result: result)
    }

    MessageResponse toResponse(Message result, MessageType type) {
        def mappedMessage = mapMessage(result)
        mappedMessage.setType(type)
        new MessageResponse(result: mappedMessage)
    }

    com.khabaznia.bot.model.Message mapMessage(Message source) {
        new com.khabaznia.bot.model.Message(messageId: source.messageId,
                chat: SessionUtil.currentChat, text: source.getText(),
                keyboard: keyboardMapper.toKeyboardModel(source.getReplyMarkup()))
    }
}
