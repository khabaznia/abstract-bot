package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString
@Component(value = 'editMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class EditMessage extends AbstractKeyboardMessage<MessageResponse> {

    String key
    String emoji
    Integer messageId
    Map<String, String> binding
    String label

    MessageType getType() {
        super.type == MessageType.EDIT_AND_DELETE ? super.type : MessageType.EDIT
    }

    EditMessage delete() {
        super.type = MessageType.EDIT_AND_DELETE
        this
    }
}
