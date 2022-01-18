package com.khabaznia.bot.controller

import com.khabaznia.bot.core.proxy.ControllerMetaData
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.meta.utils.BotRequestList
import com.khabaznia.bot.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
abstract class AbstractBotController {

    @Autowired
    ApplicationContext context
    @Autowired
    ApplicationEventPublisher publisher

    private Update update
    private List<BaseRequest> requests

    @Autowired
    UserService userService

    void before(final ControllerMetaData metaData, final Update update) {
        requests = new BotRequestList()
        this.update = update
    }

    void after(final String currentPath) {
        publisher.publishEvent new ExecuteMethodsEvent(requests: requests)
        userService.setPreviousPath currentPath
    }

    SendMessage getSendMessage() {
        def message = context.getBean 'sendMessage'
        requests.add(message)
        message
    }

    EditMessage getEditMessage() {
        def message = context.getBean 'editMessage'
        requests.add(message)
        message
    }

    void getDeleteMessages() {
    }

    InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }


}
