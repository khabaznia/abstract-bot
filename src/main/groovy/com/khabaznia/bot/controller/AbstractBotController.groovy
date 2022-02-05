package com.khabaznia.bot.controller

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.*
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.trait.Loggable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
abstract class AbstractBotController implements Configurable, Loggable {

    @Autowired
    protected ApplicationContext context
    @Autowired
    protected ApplicationEventPublisher publisher
    @Autowired
    protected UpdateService updateService

    protected Update update
    protected List<BaseRequest> requests

    @Autowired
    UserService userService

    void before(Update update) {
        setUp(update)
    }

    void after(String originalPath) {
        requests.each { it.setUpdateId(update.getUpdateId()) }
        publisher.publishEvent new ExecuteMethodsEvent(requests: requests)
        userService.setPreviousPath originalPath
    }

    protected String getAdminChatId() {
        getConfig(LoggingChat.ADMIN.chatIdConfig)
    }

    protected String getChatId() {
        SessionUtil.currentChat.code
    }

    protected SendMessage getSendMessage() {
        def message = context.getBean 'sendMessage'
        requests.add(message)
        message
    }

    protected EditMessage getEditMessage() {
        def message = context.getBean 'editMessage'
        requests.add(message)
        message
    }

    protected SendPhoto getSendPhoto() {
        def message = context.getBean 'sendPhoto'
        requests.add(message)
        message
    }

    protected SendVideo getSendVideo() {
        def message = context.getBean 'sendVideo'
        requests.add(message)
        message
    }

    protected SendAudio getSendAudio() {
        def message = context.getBean 'sendAudio'
        requests.add(message)
        message
    }

    protected InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    protected ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }

    protected void deleteOldMessages(List<MessageType> types) {
        publisher.publishEvent new DeleteMessagesEvent(types: types, updateId: update.getUpdateId())
    }

    private void setUp(Update update) {
        requests = []
        this.update = update
    }
}
