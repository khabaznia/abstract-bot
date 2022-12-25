package com.khabaznia.bot.handler

import com.khabaznia.bot.core.Constants
import com.khabaznia.bot.core.handler.UpdateBotControllerResolver
import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.enums.ChatType
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.event.*
import com.khabaznia.bot.exception.BotServiceException
import com.khabaznia.bot.service.BotMessagesService
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

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.*
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.*
import static com.khabaznia.bot.core.Constants.DELETE_PREVIOUS_INLINE_KEYBOARDS
import static com.khabaznia.bot.enums.MessageFeature.INLINE_KEYBOARD_MESSAGE_GROUP
import static com.khabaznia.bot.service.UpdateService.getParametersFromMessage
import static com.khabaznia.bot.service.UpdateService.getUpdateType
import static com.khabaznia.bot.util.HTMLParsingUtil.mapHTMLParsableSymbols
import static com.khabaznia.bot.util.SessionUtil.getCurrentChat
import static com.khabaznia.bot.util.SessionUtil.getStringAttribute

@Slf4j
@Component
class UpdateHandler implements Configurable, Loggable {

    @Autowired
    private UpdateService updateService
    @Autowired
    private ApplicationEventPublisher publisher
    @Autowired
    private UpdateBotControllerResolver botControllerResolver
    @Autowired
    private MessageService messageService
    @Autowired
    private BotMessagesService botMessagesService

    void before(Update update) {
        logToConsole(update)
        logToChat(update)
        setUpdateAttributesToSession(update)
        updateCurrentKeyboard(update)
    }

    void process(Update update) {
        def botController = botControllerResolver.getController(update)
        while (botController) {
            sendChatAction(botController)
            def path = botController.process update
            botController = path ? botControllerResolver.getController(path) : null
        }
    }

    void after(Update update) {
        deleteOldMessages(update)
        deleteCurrentOneTimeKeyboard(update)
        deleteOldInlineKeyboardMessages(update)
    }

    void handleServiceException(BotServiceException ex) {
        publisher.publishEvent new ExecuteMethodsEvent(requests: [botMessagesService.sendExceptionMessage(ex.message, ex.binding)])
    }

    private sendChatAction(BotControllerProxy botController) {
        if (canSendChatAction())
            publisher.publishEvent new SendChatActionEvent(actionType: botController.metaData.actionType)
    }

    private logToChat(Update update) {
        sendLog(new LogEvent(text: mapHTMLParsableSymbols(updateAsString(update)), logType: LogType.DEBUG))
    }

    private static logToConsole(Update update) {
        log.info '====================================================================================='
        log.debug 'Got update with id {}.', update.updateId
        log.trace "Full update -> $update."
    }

    private setUpdateAttributesToSession(Update update) {
        SessionUtil.setAttribute(UPDATE_MESSAGE, updateAsString(update))
        SessionUtil.setAttribute(UPDATE_ID, update.updateId.toString())
        SessionUtil.setAttribute(IS_UPDATE_PROCESSED, 'false')
    }

    private String updateAsString(Update update) {
        updateService.getMessageFromUpdate(update) ?: getUpdateType(update).defaultController
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
            it.features.contains(MessageFeature.ONE_TIME_INLINE_KEYBOARD) || it.features.contains(MessageFeature.INLINE_KEYBOARD)
        }
    }

    private void deleteCurrentOneTimeKeyboard(Update update) {
        def isOneTime = Boolean.valueOf(getParametersFromMessage(getStringAttribute(UPDATE_MESSAGE))?.get(ONE_TIME_KEYBOARD))
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

    private boolean canSendChatAction() {
        isEnabled(Constants.IGNORE_CHAT_ACTIONS_FOR_GROUPS)
                ? currentChat.type == ChatType.PRIVATE
                : true
    }
}
