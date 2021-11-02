package com.khabaznia.bot.meta.request

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

abstract class BaseRequest {

    Integer order
    String chatId

    String getChatId() {
        if (!chatId) {
            // TODO get chat id
        }
        chatId
    }

    abstract BotApiMethod toApiMethod()
}
