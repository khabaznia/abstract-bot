package com.khabaznia.bots.core.meta.object

import com.khabaznia.bots.core.enums.Scope
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.stereotype.Component

@Component(value = 'botCommandScope')
@org.springframework.context.annotation.Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor
@ToString
class BotCommandScope {

    Scope type = Scope.DEFAULT
    String chatId
    String userId
}
