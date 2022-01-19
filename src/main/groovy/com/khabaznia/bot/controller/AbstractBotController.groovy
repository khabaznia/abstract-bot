package com.khabaznia.bot.controller

import com.khabaznia.bot.core.proxy.ControllerMetaData
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.DeleteOneTimeKeyboardMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.meta.utils.BotRequestList
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.*

@Slf4j
abstract class AbstractBotController {

    @Autowired
    ApplicationContext context
    @Autowired
    ApplicationEventPublisher publisher
    @Autowired
    UpdateService updateService

    private Update update
    private List<BaseRequest> requests

    @Autowired
    UserService userService

    void before(final ControllerMetaData metaData, final Update update) {
        requests = new BotRequestList()
        this.update = update
        deleteMessages()
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

    void deleteMessages() {
        def deleteOneTimeKeyboardMessageCode = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_CODE)
        if (deleteOneTimeKeyboardMessageCode){
            publisher.publishEvent new DeleteOneTimeKeyboardMessagesEvent(code: deleteOneTimeKeyboardMessageCode as Long)
        }
        publisher.publishEvent new DeleteMessagesEvent()
    }

    InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }


}
