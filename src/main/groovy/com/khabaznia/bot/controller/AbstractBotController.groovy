package com.khabaznia.bot.controller

import com.khabaznia.bot.core.proxy.ControllerMetaData
import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.DeleteOneTimeKeyboardMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.event.UpdateKeyboardEvent
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.EditMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.meta.utils.BotRequestList
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.*
import static com.khabaznia.bot.core.Constants.*

@Slf4j
abstract class AbstractBotController implements Configured{

    @Autowired
    ApplicationContext context
    @Autowired
    ApplicationEventPublisher publisher
    @Autowired
    UpdateService updateService

    protected Update update
    protected List<BaseRequest> requests

    @Autowired
    UserService userService

    void before(final ControllerMetaData metaData, final Update update) {
        requests = new BotRequestList()
        this.update = update
        deleteMessages()
        cleanCurrentOneTimeKeyboard()
        updateCurrentKeyboard()
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
        publisher.publishEvent new DeleteMessagesEvent()
    }

    private void cleanCurrentOneTimeKeyboard() {
        def isOneTime = Boolean.valueOf(updateService?.getParametersFromUpdate(update)?.get(ONE_TIME_KEYBOARD))
        if (isOneTime) {
            def messageCode = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_CODE)
            publisher.publishEvent new DeleteOneTimeKeyboardMessagesEvent(code: messageCode as Long)
        }
    }

    void updateCurrentKeyboard() {
        if (hasSpecialButtonParams()) {
            log.debug 'Try to update keyboard'
            def messageCode = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_CODE)
            def buttonId = updateService?.getParametersFromUpdate(update)?.get(BUTTON_ID)
            publisher.publishEvent new UpdateKeyboardEvent(code: messageCode as Long, buttonId: buttonId)
        }
    }

    private boolean hasSpecialButtonParams() {
        !ButtonType.values()
                .collect { it.paramKey }
                .findAll { !it.isBlank() }
                .findAll { updateService?.getParametersFromUpdate(update)?.containsKey(it) }
                .isEmpty()
    }

    InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }


}
