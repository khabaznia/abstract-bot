package com.khabaznia.bots.core.meta.request.impl

import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.response.impl.BooleanResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'deleteMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class DeleteMessage extends BaseRequest<BooleanResponse> {

    Integer messageId
    String label //label of message that should be in DB
}
