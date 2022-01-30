package com.khabaznia.bot.controller

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
import com.khabaznia.bot.trait.Logged
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.*
import static com.khabaznia.bot.core.Constants.DELETE_PREVIOUS_INLINE_KEYBOARDS

@Slf4j
abstract class AbstractBotController implements Configured, Logged {

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
//        SessionUtil.setAttribute('updateMessage', updateService.getMessageFromUpdate(update))
        deleteMessages()
        cleanCurrentOneTimeKeyboard()
        updateCurrentKeyboard()
    }

    void after(String originalPath) {
        deleteOldInlineKeyboardMessages()
        publisher.publishEvent new ExecuteMethodsEvent(requests: requests)
        userService.setPreviousPath originalPath
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

    InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }

    private void setUp(Update update) {
        requests = new BotRequestList()
        this.update = update
    }

    void deleteMessages() {
        publisher.publishEvent new DeleteMessagesEvent()
    }

    void deleteMessages(MessageType type) {
        publisher.publishEvent new DeleteMessagesEvent(type: type)
    }

    void deleteOldInlineKeyboardMessages() {
        if (isEnabled(DELETE_PREVIOUS_INLINE_KEYBOARDS) && hasInlineKeyboard()) {
            publisher.publishEvent new DeleteMessagesEvent(type: MessageType.INLINE_KEYBOARD)
            publisher.publishEvent new DeleteMessagesEvent(type: MessageType.ONE_TIME_INLINE_KEYBOARD)
        }
    }

    private Boolean hasInlineKeyboard() {
        requests.any {
            it.type == MessageType.ONE_TIME_INLINE_KEYBOARD || it.type == MessageType.INLINE_KEYBOARD
        }
    }

    private void cleanCurrentOneTimeKeyboard() {
        def isOneTime = Boolean.valueOf(updateService?.getParametersFromUpdate(update)?.get(ONE_TIME_KEYBOARD))
        if (isOneTime) {
            def messageUid = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_UID)
            publisher.publishEvent new DeleteOneTimeKeyboardMessagesEvent(messageUid: messageUid)
        }
    }

    void updateCurrentKeyboard() {
        if (hasSpecialButtonParams()) {
            log.debug 'Try to update keyboard'
            def messageCode = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_UID)
            def buttonId = updateService?.getParametersFromUpdate(update)?.get(BUTTON_ID)
            publisher.publishEvent new UpdateKeyboardEvent(messageUid: messageCode, buttonId: buttonId)
        }
    }

    private boolean hasSpecialButtonParams() {
        !ButtonType.values()
                .collect { it.paramKey }
                .findAll { !it.isBlank() }
                .findAll { updateService?.getParametersFromUpdate(update)?.containsKey(it) }
                .isEmpty()
    }

}
