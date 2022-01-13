package com.khabaznia.bot.controller

import com.khabaznia.bot.core.proxy.ControllerMetaData
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
abstract class AbstractBotController {

    Update update;

    @Autowired
    UserService userService

    void before(final ControllerMetaData metaData, final Update update) {
        this.update = update
    }

    void after(final String currentPath) {
//        publisher.publishEvent new ExecuteMethodsEvent(requests: requests)
        userService.setPreviousPath currentPath
    }

    SendMessage getSendMessage() {
        return new SendMessage()
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
