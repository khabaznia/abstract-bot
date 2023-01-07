package com.khabaznia.bots.core.meta.request.impl

import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@ToString(includeSuper = true, includeNames = true)
@Component(value = 'sendAudio')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class SendAudio extends AbstractMediaRequest<MessageResponse> {
}
