package com.khabaznia.bots.core.meta.keyboard.impl

import com.khabaznia.bots.core.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.enums.ButtonType
import com.khabaznia.bots.core.meta.keyboard.Keyboard
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bots.common.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU
import static com.khabaznia.bots.core.meta.Emoji.CHECKED_MARK
import static com.khabaznia.bots.core.meta.Emoji.CROSS_MARK
import static com.khabaznia.bots.core.util.KeyboardUtil.fillButton

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

    InlineKeyboard addButton(InlineButton button) {
        currentRow.add button
        this
    }

    InlineKeyboard addButtons(List<InlineButton> buttons, boolean separateRows = false) {
        buttons.findAll().each { currentRow.add it }
        if (separateRows) row()
        this
    }

    InlineKeyboard button(String text, String callbackData) {
        def button = button
                .callbackData(callbackData)
                .text(text)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String text, String emoji, String callbackData) {
        def button = button
                .callbackData(callbackData)
                .text(text)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard buttonWithBinding(String text, String callbackData, Map<String, String> binding) {
        def button = button
                .callbackData(callbackData)
                .text(text)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String text, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .text(text)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String text, String emoji, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(emoji)
                .text(text)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String text, String emoji, Map<String, String> binding, String callbackData, Map<String, String> params) {
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(emoji)
                .text(text)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard switchButton(String text, String callbackData, Boolean isEnabled, Map<String, String> params) {
        params.put(ButtonType.SWITCH.paramKey, isEnabled as String)
        params.put(UNLIMITED_CALL, true.toString())
        def button = button
                .callbackData(callbackData)
                .params(params)
                .emoji(isEnabled ? CHECKED_MARK : CROSS_MARK)
                .text(text)
                .type(ButtonType.SWITCH)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String text, String callbackData) {
        def params = [(ButtonType.ONE_TIME.paramKey): 'true']
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .text(text)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String text, String emoji, String callbackData) {
        def params = [(ButtonType.ONE_TIME.paramKey): 'true']
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .text(text)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String text, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .text(text)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String key, String emoji, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .text(key)
                .emoji(emoji)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard oneTimeButton(String text, String emoji, Map<String, String> binding, String callbackData, Map<String, String> params) {
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(callbackData)
                .params(params)
                .type(ButtonType.ONE_TIME)
                .text(text)
                .emoji(emoji)
                .binding(binding)
        currentRow.add button as InlineButton
        this
    }

    InlineKeyboard button(String text, String emoji = null, ConfirmationFlowDto confirmationFlowDto) {
        currentRow.add fillButton(button, text, confirmationFlowDto, emoji)
        this
    }

    InlineKeyboard oneTimeButton(String text, ConfirmationFlowDto confirmationFlowDto) {
        def params = getConfirmationFlowButtonParams(confirmationFlowDto)
        params.put(ButtonType.ONE_TIME.paramKey, 'true')
        def button = button
                .callbackData(CONFIRMATION_MENU)
                .params(params)
                .text(text)
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

    private static Map<String, String> getConfirmationFlowButtonParams(ConfirmationFlowDto confirmationFlowDto) {
        confirmationFlowDto.getClass()
                .declaredFields
                .findAll { !it.synthetic }
                .findAll { it.name != "params" }
                .findAll { it.name != 'menuTextBinding' }
                .collectEntries { field ->
                    [field.name, confirmationFlowDto."$field.name"]
                } << confirmationFlowDto.params << confirmationFlowDto.menuTextBinding
    }
}
