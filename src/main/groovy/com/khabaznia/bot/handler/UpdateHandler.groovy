package com.khabaznia.bot.handler

import com.khabaznia.bot.core.handler.MessageToCommandMapper
import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.DeleteOneTimeKeyboardMessagesEvent
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.event.SendChatActionEvent
import com.khabaznia.bot.event.UpdateKeyboardEvent
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.trait.Loggable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.BUTTON_ID
import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.MESSAGE_UID
import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.ONE_TIME_KEYBOARD
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.IS_UPDATE_PROCESSED_ATTR
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID_ATTR
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE_ATTR
import static com.khabaznia.bot.core.Constants.DELETE_PREVIOUS_INLINE_KEYBOARDS
import static com.khabaznia.bot.enums.MessageType.INLINE_KEYBOARD_MESSAGE_GROUP
import static com.khabaznia.bot.service.UpdateService.getUpdateType

@Slf4j
@Component
class UpdateHandler implements Configurable, Loggable {

    @Autowired
    private UpdateService updateService
    @Autowired
    private ApplicationEventPublisher publisher
    @Autowired
    private MessageToCommandMapper commandMapper
    @Autowired
    private MessageService messageService

    void before(Update update) {
        logToConsole(update)
        logToChat(update)
        setUpdateAttributesToSession(update)
        updateCurrentKeyboard(update)
    }

    void process(Update update) {
        def botController = commandMapper.getController(update)
        while (botController) {
            sendChatAction(botController)
            def path = botController.process update
            botController = path ? commandMapper.getController(path) : null
        }
    }

    void after(Update update) {
        deleteOldMessages(update)
        deleteCurrentOneTimeKeyboard(update)
        deleteOldInlineKeyboardMessages(update)
    }

    private sendChatAction(BotControllerProxy botController) {
        publisher.publishEvent new SendChatActionEvent(actionType: botController.metaData.actionType)
    }

    private logToChat(Update update) {
        sendLog(new LogEvent(text: updateService.getMessageFromUpdate(update), logType: LogType.DEBUG))
    }

    private static logToConsole(Update update) {
        log.info '====================================================================================='
        log.debug 'Got update with id {}. Has message -> {}', update.updateId, update.hasMessage()
        log.trace "Full update -> $update"
    }

    private setUpdateAttributesToSession(Update update) {
        SessionUtil.setAttribute(UPDATE_MESSAGE_ATTR, updateService.getMessageFromUpdate(update) ?: getUpdateType(update).defaultController)
        SessionUtil.setAttribute(UPDATE_ID_ATTR, update.updateId.toString())
        SessionUtil.setAttribute(IS_UPDATE_PROCESSED_ATTR, 'false')
    }

    private void updateCurrentKeyboard(Update update) {
        if (hasSpecialButtonParams(update)) {
            log.debug 'Try to update keyboard'
            def messageCode = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_UID)
            def buttonId = updateService?.getParametersFromUpdate(update)?.get(BUTTON_ID)
            log.debug 'Trigger updating button {} for keyboard message {}', buttonId, messageCode
            publisher.publishEvent new UpdateKeyboardEvent(messageUid: messageCode, buttonId: buttonId)
        }
    }

    private void deleteOldMessages(Update update) {
        publisher.publishEvent new DeleteMessagesEvent(updateId: update.updateId)
    }

    private void deleteOldInlineKeyboardMessages(Update update) {
        if (isEnabled(DELETE_PREVIOUS_INLINE_KEYBOARDS) && hasInlineKeyboard(update.getUpdateId())) {
            publisher.publishEvent new DeleteMessagesEvent(types: INLINE_KEYBOARD_MESSAGE_GROUP, updateId: update.getUpdateId())
        }
    }

    private Boolean hasInlineKeyboard(Integer currentUpdateId) {
        messageService.getMessagesForUpdate(currentUpdateId).any {
            it.type == MessageType.ONE_TIME_INLINE_KEYBOARD || it.type == MessageType.INLINE_KEYBOARD
        }
    }

    private void deleteCurrentOneTimeKeyboard(Update update) {
        def isOneTime = Boolean.valueOf(updateService?.getParametersFromUpdate(update)?.get(ONE_TIME_KEYBOARD))
        if (isOneTime) {
            def messageUid = updateService?.getParametersFromUpdate(update)?.get(MESSAGE_UID)
            log.debug 'Trigger deleting on-time keyboard. Message id -> ', {}
            publisher.publishEvent new DeleteOneTimeKeyboardMessagesEvent(messageUid: messageUid)
        }
    }

    private boolean hasSpecialButtonParams(Update update) {
        !ButtonType.values()
                .collect { it.paramKey }
                .findAll { !it.isBlank() }
                .findAll { updateService?.getParametersFromUpdate(update)?.containsKey(it) }
                .isEmpty()
    }
}
