package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bot.enums.MessageType.getForceDeleteGroup

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'sendMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class SendMessage extends AbstractKeyboardMessage<MessageResponse> {

    String key
    String emoji
    Map<String, String> binding = [:]
    String label

    MessageType getType() {
        label && getForceDeleteGroup().contains(super.type) ? MessageType.PERSIST : super.type
    }
}
