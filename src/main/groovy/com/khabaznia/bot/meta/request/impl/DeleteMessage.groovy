package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.BooleanResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

@ToString(includeSuper = true)
@Component(value = 'deleteMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class DeleteMessage extends BaseRequest<BooleanResponse> {

    Integer messageId

    DeleteMessage setMessageId(Integer messageId){
        this.messageId = messageId
        return this
    }
}
