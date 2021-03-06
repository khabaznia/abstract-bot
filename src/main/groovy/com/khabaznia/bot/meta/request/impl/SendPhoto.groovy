package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'sendPhoto')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class SendPhoto extends BaseRequest<MessageResponse> {

    String photo
    String text
    String emoji
    Map<String, String> binding = [:]

    MessageType getType() {
        super.type ?: MessageType.MEDIA
    }
}