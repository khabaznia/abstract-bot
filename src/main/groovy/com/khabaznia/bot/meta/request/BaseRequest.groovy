package com.khabaznia.bot.meta.request

import com.khabaznia.bot.enums.MessageFeature
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

    protected static final String MOCK_CHAT_ID = 'unusedChatId'

    Integer order = BotRequestQueueContainer.requestOrder.getAndIncrement()
    String chatId = SessionUtil.currentChat?.code
    Set<MessageFeature> features = []
    String relatedMessageUid
    Object apiMethod
    Integer updateId
    boolean isOmissible = false
    boolean disableNotification = false

    BaseRequest delete() {
        features << MessageFeature.DELETE
        this
    }

    BaseRequest persist() {
        features << MessageFeature.PERSIST
        this
    }

    BaseRequest feature(MessageFeature feature){
        features << feature
        this
    }
}
