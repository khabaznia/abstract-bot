package com.khabaznia.bot.meta.request

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.trait.Localized
import com.khabaznia.bot.util.SessionUtil
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
abstract class BaseRequest<T extends BaseResponse> {

    Integer order
    String chatId = SessionUtil.currentChat?.code
    MessageType type = MessageType.SKIP

    BaseRequest delete() {
        type = MessageType.DELETE
        this
    }
}
