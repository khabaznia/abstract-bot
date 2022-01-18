package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true)
@Component(value = 'replyKeyboard')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class ReplyKeyboard extends Keyboard<ReplyButton> {

    List<List<ReplyButton>> lastRows

    ReplyKeyboard() {
        this.rows = [[]]
        this.lastRows = [[]]
        this.currentRow = []
    }

    ReplyKeyboard button(final String key) {
        def button = button.key(key)
        currentRow.add button
        this
    }

    ReplyKeyboard button(final String key, final Map<String, String> binding) {
        def button = button.key(key).binding(binding)
        currentRow.add button
        this
    }

    ReplyKeyboard rowToEnd() {
        lastRows?.add currentRow
        currentRow = []
        this
    }

    @Override
    ReplyKeyboard row() {
        rows?.add currentRow
        currentRow = []
        this
    }

    @Override
    List<List<ReplyButton>> get() {
        rows?.add currentRow
        rows?.addAll lastRows
        return rows
    }

    @Override
    protected ReplyButton getButton() {
        context.getBean('replyButton')
    }
}
