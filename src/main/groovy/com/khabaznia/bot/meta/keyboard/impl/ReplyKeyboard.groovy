package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString
@Component
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class ReplyKeyboard extends Keyboard {

    @Override
    ReplyKeyboard addRow() {
        return null
    }

    ReplyKeyboard addButton(String name) {
        return null
    }
}
