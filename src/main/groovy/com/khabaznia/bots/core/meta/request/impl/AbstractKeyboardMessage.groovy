package com.khabaznia.bots.core.meta.request.impl

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.meta.keyboard.Keyboard
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@ToString(includeSuper = true, includeNames = true)
abstract class AbstractKeyboardMessage<T extends MessageResponse> extends BaseRequest {

    @Autowired
    protected ApplicationContext context

    Keyboard keyboard

    AbstractKeyboardMessage keyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this.feature(keyboard instanceof InlineKeyboard ? MessageFeature.INLINE_KEYBOARD : MessageFeature.REPLY_KEYBOARD)
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this.feature(MessageFeature.ONE_TIME_INLINE_KEYBOARD)
        this
    }

    AbstractKeyboardMessage keyboard(Map<String, String> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each { inlineKeyboard.button(it.key, it.value) }
        keyboard = inlineKeyboard
        this.feature(MessageFeature.INLINE_KEYBOARD)
        this
    }

    AbstractKeyboardMessage inlineKeyboard(List<Map<String, String>> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each {
            it.each { inlineKeyboard.button(it.key, it.value) }
            inlineKeyboard.row()
        }
        keyboard = inlineKeyboard
        this.feature(MessageFeature.INLINE_KEYBOARD)
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(Map<String, String> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each { inlineKeyboard.button(it.key, it.value) }
        keyboard = inlineKeyboard
        this.feature(MessageFeature.ONE_TIME_INLINE_KEYBOARD)
        this
    }

    AbstractKeyboardMessage oneTimeKeyboard(List<Map<String, String>> buttons) {
        def inlineKeyboard = context.getBean('inlineKeyboard')
        buttons.each {
            it.each { inlineKeyboard.button(it.key, it.value) }
            inlineKeyboard.row()
        }
        keyboard = inlineKeyboard
        this.feature(MessageFeature.ONE_TIME_INLINE_KEYBOARD)
        this
    }

    AbstractKeyboardMessage keyboard(List<String> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each { replyKeyboard.button(it) }
        keyboard = replyKeyboard
        this.feature(MessageFeature.REPLY_KEYBOARD)
        this
    }

    AbstractKeyboardMessage replyKeyboard(List<List<String>> keys) {
        def replyKeyboard = context.getBean('replyKeyboard')
        keys.each {
            it.each { replyKeyboard.button(it) }
            replyKeyboard.row()
        }
        keyboard = replyKeyboard
        this.feature(MessageFeature.REPLY_KEYBOARD)
        this
    }
}
