package com.khabaznia.bot.meta.keyboard.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.*
import static com.khabaznia.bot.meta.Emoji.*
import static com.khabaznia.bot.core.Constants.PARAMETERS_PREFIX

@ToString
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

    InlineKeyboard button(String key, String callbackData) {
        def button = button.key(key).callbackData(callbackData)
        currentRow.add button
        this
    }

    InlineKeyboard button(String key, String emoji, String callbackData) {
        def button = button.key(key)
                .callbackData(callbackData)
                .emoji(emoji)
        currentRow.add button
        this
    }

    InlineKeyboard buttonWithBinding(String key, String callbackData, Map<String, String> binding) {
        def button = button.key(key)
                .binding(binding)
                .callbackData(callbackData)
        currentRow.add button
        this
    }

    InlineKeyboard button(String key, String callbackData, Map<String, String> params) {
        def button = button.key(key)
                .callbackData(callbackData)
                .params(params)
        currentRow.add button
        this
    }

    InlineKeyboard button(String key, String emoji, String callbackData, Map<String, String> params) {
        def button = button.key(key)
                .callbackData(callbackData)
                .params(params)
                .emoji(emoji)
        currentRow.add button
        this
    }

    InlineKeyboard switchButton(String key, String callbackData, Boolean isEnabled, Map<String, String> params) {
        params.put(ENABLED, isEnabled.toString())
        def button = button.key(key)
                .callbackData(callbackData)
                .params(params)
                .emoji(isEnabled ? CHECKED_MARK : CROSS_MARK)
        currentRow.add button
        this
    }

    void addKeyboardParam(String key, String value) {
        keyboardParams.put(key, value)
    }

    Map<String, String> getKeyboardParams() {
        keyboardParams
    }
//
//    InlineKeyboard oneTimeButton(String key, String callbackData) {
//        button(key, callbackData, [(ONE_TIME): 'true'])
//    }
//
//    InlineKeyboard oneTimeButton(String key, String emoji, String callbackData) {
//        button(key, emoji, callbackData, [(ONE_TIME): 'true'])
//    }
//
//    InlineKeyboard oneTimeButton(String key, String callbackData, Map<String, String> params) {
//        params.put(ONE_TIME, 'true')
//        button(key, callbackData, params)
//    }
//
//    InlineKeyboard oneTimeButton(String key, String emoji, String callbackData, Map<String, String> params) {
//        params.put(ONE_TIME, 'true')
//        button(key, emoji, callbackData, params)
//    }

    InlineKeyboard deleteButtonForPath(final String buttonPath) {
        rows.each {
            it.removeAll { it.callbackData.startsWith(buttonPath) }
        }
        currentRow.removeAll { it.callbackData.startsWith(buttonPath) }
        this
    }

    InlineKeyboard cleanKeyboardByOptions(final List<String> options) {
        rows.each {
            it.removeAll { !options.contains(it.callbackData.split(PARAMETERS_PREFIX)[0]) }
        }
        currentRow.removeAll { !options.contains(it.callbackData.split(PARAMETERS_PREFIX)[0]) }
        this
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
}
