package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.keyboard.Keyboard
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@ToString
abstract class AbstractKeyboardMessage<T extends MessageResponse> extends BaseRequest {

    @Autowired
    protected ApplicationContext context

    Keyboard keyboard

    AbstractKeyboardMessage keyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this.type = keyboard instanceof InlineKeyboard ? MessageType.INLINE_KEYBOARD : MessageType.REPLY_KEYBOARD
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this.type = MessageType.ONE_TIME_INLINE_KEYBOARD
        this
    }

    AbstractKeyboardMessage keyboard(Map<String, String> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each { inlineKeyboard.button(it.key, it.value) }
        keyboard = inlineKeyboard
        this.type = MessageType.INLINE_KEYBOARD
        this
    }

    AbstractKeyboardMessage inlineKeyboard(List<Map<String, String>> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each {
            it.each { inlineKeyboard.button(it.key, it.value) }
            inlineKeyboard.row()
        }
        keyboard = inlineKeyboard
        this.type = MessageType.INLINE_KEYBOARD
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(Map<String, String> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each { inlineKeyboard.button(it.key, it.value) }
        keyboard = inlineKeyboard
        this.type = MessageType.ONE_TIME_INLINE_KEYBOARD
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(List<Map<String, String>> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each {
            it.each { inlineKeyboard.button(it.key, it.value) }
            inlineKeyboard.row()
        }
        keyboard = inlineKeyboard
        this.type = MessageType.ONE_TIME_INLINE_KEYBOARD
        this
    }

    AbstractKeyboardMessage keyboard(List<String> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each { replyKeyboard.button(it) }
        keyboard = replyKeyboard
        this.type = MessageType.REPLY_KEYBOARD
        this
    }

    AbstractKeyboardMessage replyKeyboard(List<List<String>> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each {
            it.each { replyKeyboard.button(it) }
            replyKeyboard.row()
        }
        keyboard = replyKeyboard
        this.type = MessageType.REPLY_KEYBOARD
        this
    }
}
