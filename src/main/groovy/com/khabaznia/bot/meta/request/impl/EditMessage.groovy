package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bot.enums.MessageType.getEditGroup

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'editMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class EditMessage extends AbstractKeyboardMessage<MessageResponse> {

    String text
    String emoji
    Integer messageId
    Map<String, String> binding
    String label

    MessageType getType() {
        getEditGroup().contains(super.type) ? super.type : MessageType.EDIT
    }

    EditMessage delete() {
        super.type = MessageType.EDIT_AND_DELETE
        this
    }
}
