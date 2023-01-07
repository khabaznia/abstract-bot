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
@Component(value = 'batChatMember')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class BanChatMember extends BaseRequest<BooleanResponse> {

    String userId
    Boolean revokeMessages = true
}
