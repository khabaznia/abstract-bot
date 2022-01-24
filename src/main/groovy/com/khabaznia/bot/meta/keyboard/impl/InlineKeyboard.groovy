package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.CHECKED_MARK
import static com.khabaznia.bot.meta.Emoji.CROSS_MARK

@Component(value = 'inlineKeyboard')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class InlineKeyboard extends Keyboard<InlineButton> {

    private Map<String, String> keyboardParams

    InlineKeyboard() {
        this.keyboardParams = [:]
        this.rows = [[]]
        this.currentRow = []
    }

    InlineKeyboard button(InlineButton button) {
        currentRow.add button
        this
    }

    InlineKeyboard button(String key, String callbackData) {
        def button = button
                .callbackData(callbackData)
                .key(key)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String key, String emoji, String callbackData) {
        def button = button
                .callbackData(callbackData)
                .key(key)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard buttonWithBinding(String key, String callbackData, Map<String, String> binding) {
        def button = button
                .callbackData(callbackData)
                .key(key)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String key, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .key(key)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String key, String emoji, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(emoji)
                .key(key)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String key, String emoji, Map<String, String> binding, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(emoji)
                .key(key)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard switchButton(String key, String callbackData, Boolean isEnabled, Map<String, String> params) {
        params.put(ButtonType.SWITCH.paramKey, isEnabled as String)
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(isEnabled ? CHECKED_MARK : CROSS_MARK)
                .key(key)
                .type(ButtonType.SWITCH)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String callbackData) {
        def params = [(ButtonType.ONE_TIME.paramKey): 'true']
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .key(key)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String emoji, String callbackData) {
        def params = [(ButtonType.ONE_TIME.paramKey): 'true']
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .key(key)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .key(key)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String emoji, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .key(key)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String emoji, Map<String, String> binding, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .key(key)
                .emoji(emoji)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    void addKeyboardParam(String key, String value) {
        keyboardParams.put(key, value)
    }

    Map<String, String> getKeyboardParams() {
        keyboardParams
    }

    @Override
    InlineKeyboard row() {
        rows?.add currentRow
        currentRow = []
        this
    }

    @Override
    List<List<InlineButton>> get() {
        row()
        rows
                .findAll { it != null }
                .findAll { it -> !it.isEmpty() }
    }

    @Override
    protected InlineButton getButton() {
        context.getBean('inlineButton')
    }

    @Override
    String toString() {
        get().collect {
            "row: { " + it + " } \n"
        }
    }
}
