package com.khabaznia.bot.meta.request

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject

@ToString
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class BaseResponse {

    BotApiObject response
}
