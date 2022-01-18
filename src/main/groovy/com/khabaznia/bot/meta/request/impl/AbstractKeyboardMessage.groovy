package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.MessageResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

abstract class AbstractKeyboardMessage<T extends MessageResponse> extends BaseRequest {

    @Autowired
    ApplicationContext context

    Keyboard keyboard

    AbstractKeyboardMessage keyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this
    }

    AbstractKeyboardMessage keyboard(List<String> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each { replyKeyboard.button(it) }
        keyboard = replyKeyboard
        this
    }

    AbstractKeyboardMessage replyKeyboard(List<List<String>> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each {
            it.each { replyKeyboard.button(it) }
            replyKeyboard.row()
        }
        keyboard = replyKeyboard
        this
    }

    AbstractKeyboardMessage keyboard(Map<String, String> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each { inlineKeyboard.button(it.key, it.value) }
        keyboard = inlineKeyboard
        this
    }

    AbstractKeyboardMessage inlineKeyboard(List<Map<String, String>> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each {
            it.each { inlineKeyboard.button(it.key, it.value) }
            inlineKeyboard.row()
        }
        keyboard = inlineKeyboard
        this
    }
}
