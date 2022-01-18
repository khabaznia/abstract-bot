package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Button
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true)
@Component(value = 'replyButton')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor
class ReplyButton extends Button {

    String key
    Map<String, String> binding
    String emoji
}
