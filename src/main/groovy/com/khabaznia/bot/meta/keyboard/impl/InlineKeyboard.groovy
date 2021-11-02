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
class InlineKeyboard extends Keyboard {

    @Override
    InlineKeyboard addRow() {
        return null
    }

    InlineKeyboard addButton(String name, String callBackData) {
        return null
    }

    InlineKeyboard addButton(String name, String callBackData, Map<String, String> params) {
        return null
    }
}
