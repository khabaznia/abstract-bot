package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.impl.BooleanResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class ResponseMapper {

    @Autowired
    KeyboardMapper keyboardMapper

    BooleanResponse toResponse(Boolean result) {
        new BooleanResponse(result: result)
    }

    MessageResponse toResponse(Message result) {
        new MessageResponse(result: new com.khabaznia.bot.meta.response.impl.Message(
                messageId: result.messageId,
                text: result.text))
    }

}
