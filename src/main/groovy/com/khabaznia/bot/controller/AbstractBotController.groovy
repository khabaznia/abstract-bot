package com.khabaznia.bot.controller

import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import groovy.util.logging.Slf4j

@Slf4j
abstract class AbstractBotController {

    SendMessage getSendMessage() {

        return null
    }

    EditMessage getEditMessage() {

        return null
    }

    void getDeleteMessages() {


    }

    InlineKeyboard getInlineKeyboard() {

        return null
    }

    ReplyKeyboard getReplyKeyboard() {

        return null
    }

}
