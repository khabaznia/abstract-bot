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
@Component(value = 'sendMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class SendMessage extends AbstractKeyboardMessage<MessageResponse> {

    private static final List<MessageType> FORCE_DELETE_MESSAGE_TYPES = [MessageType.SKIP, MessageType.DELETE]

    String key
    String emoji
    Map<String, String> binding
    String label

    SendMessage() {
        binding = [:]
    }

    MessageType getType() {
        label && FORCE_DELETE_MESSAGE_TYPES.contains(super.type) ? MessageType.PERSIST : super.type
    }
}
