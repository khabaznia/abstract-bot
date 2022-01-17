package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
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

    String key
    Map<String, String> binding

    SendMessage() {
        binding = [:]
    }

    @Override
    SendMessage keyboard(Keyboard keyboard) {
        super.keyboard(keyboard) as SendMessage
    }

    @Override
    SendMessage keyboard(List<String> keys) {
        super.keyboard(keys) as SendMessage
    }

    @Override
    SendMessage keyboard(Map<String, String> keys) {
        super.keyboard(keys) as SendMessage
    }
}
