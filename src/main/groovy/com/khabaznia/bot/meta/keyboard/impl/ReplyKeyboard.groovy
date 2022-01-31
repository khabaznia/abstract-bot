package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.PARAMETERS_PREFIX

@Component(value = 'replyKeyboard')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class ReplyKeyboard extends Keyboard<ReplyButton> {

    protected List<List<ReplyButton>> lastRows

    ReplyKeyboard() {
        this.rows = [[]]
        this.lastRows = [[]]
        this.currentRow = []
    }

    ReplyKeyboard button(String text) {
        def button = getButton().key(getKeyFromText(text)).emoji(getEmojiFromText(text))
        currentRow.add button as ReplyButton
        this
    }

    private static getKeyFromText(String text) {
        text.tokenize(PARAMETERS_PREFIX)[0] ?: text
    }

    private static getEmojiFromText(String text) {
        text.tokenize(PARAMETERS_PREFIX)[1]
    }

    ReplyKeyboard button(String key, String emoji) {
        def button = button.key(key).emoji(emoji)
        currentRow.add button as ReplyButton
        this
    }

    ReplyKeyboard button(String key, Map<String, String> binding) {
        def button = button.key(key).binding(binding)
        currentRow.add button as ReplyButton
        this
    }

    ReplyKeyboard button(String key, String emoji, Map<String, String> binding) {
        def button = button.key(key).emoji(emoji).binding(binding)
        currentRow.add button as ReplyButton
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
        row()
        rows?.addAll lastRows
        lastRows = [[]]
        rows
                .findAll { it != null }
                .findAll { it -> !it.isEmpty() }
    }

    @Override
    protected ReplyButton getButton() {
        context.getBean('replyButton')
    }

    @Override
    String toString() {
        get().collect {
            "row: { " + it + " } \n"
        }
    }
}
