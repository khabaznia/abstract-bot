package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.ChatMemberResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'getChatMember')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class GetChatMember extends BaseRequest<ChatMemberResponse> {

    String userId
}
