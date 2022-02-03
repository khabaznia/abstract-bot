package com.khabaznia.bot.meta.request

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.sender.BotRequestQueueContainer
import com.khabaznia.bot.util.SessionUtil
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString(includeNames = true)
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
abstract class BaseRequest<T extends BaseResponse> {

    Integer order = BotRequestQueueContainer.requestOrder.getAndIncrement()
    String chatId = SessionUtil.currentChat?.code
    MessageType type = MessageType.SKIP
    String relatedMessageUid
    Object apiMethod
    Integer updateId

    BaseRequest delete() {
        type = MessageType.DELETE
        this
    }
}
