package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.keyboard.Keyboard
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.BaseRequest

abstract class AbstractKeyboardMessage extends BaseRequest {

    Keyboard keyboard

    AbstractKeyboardMessage keyboard(Keyboard keyboard) {
        this.keyboard = keyboard
        this
    }

    AbstractKeyboardMessage keyboard(List<String> keys) {
        //TODO
        keyboard = new ReplyKeyboard()
        this
    }

    AbstractKeyboardMessage keyboard(Map<String, String> keys) {
        //TODO
        keyboard = new InlineKeyboard()
        this
    }
}
