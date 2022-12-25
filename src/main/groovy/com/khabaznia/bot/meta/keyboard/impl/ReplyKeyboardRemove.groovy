package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'replyKeyboardRemove')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class ReplyKeyboardRemove extends Keyboard<ReplyButton> {

    boolean removeKeyboard = true

    @Override
    Keyboard row() { this }

    @Override
    List<List<ReplyButton>> get() { [] }

    @Override
    protected ReplyButton getButton() { null }
}
