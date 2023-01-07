package com.khabaznia.bots.common.controller.admin

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.routing.annotation.*
import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.enums.Role
import com.khabaznia.bots.core.service.UpdateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.ADMIN_CONTROLLER.*
import static com.khabaznia.bots.core.routing.Constants.CHECK_MESSAGES_IN_LOGGING_CHAT
import static com.khabaznia.bots.core.enums.MessageFeature.INLINE_KEYBOARD_MESSAGE_GROUP
import static com.khabaznia.bots.core.meta.Emoji.*
import static com.khabaznia.bots.common.util.DefaultRoleKeyboardsUtil.adminReplyKeyboard
import static com.khabaznia.bots.core.util.SessionUtil.currentChat

@Slf4j
@Component
@BotController
class AdminController extends AbstractBotController {

    @Autowired
    private UpdateService updateService

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = ADMIN_START)
    String onStart() {
        sendMessage.text('text.admin.greeting')
                .emoji(CROWN)
                .delete()
        ADMIN_TO_MAIN
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = ADMIN_TO_MAIN)
    adminMenu() {
        deleteOldMessages(INLINE_KEYBOARD_MESSAGE_GROUP)
        sendMessage.text('text.choose.action')
                .emoji(FINGER_DOWN)
                .replyKeyboard(getAdminReplyKeyboard())
    }

    @Secured(roles = Role.ADMIN)
    @Localized
    @BotRequest(path = FEATURES_LIST)
    getFeatures() {
        def featuresKeyboard = inlineKeyboard
        switchableConfigs.each { featuresKeyboard.switchButton(it.name, SWITCH_FEATURE, Boolean.valueOf(it.value), [configKey: it.key]).row() }
        featuresKeyboard.button('button.back', LEFT_ARROW, ADMIN_TO_MAIN)
        sendMessage.text('text.admin.switch.features')
                .keyboard(featuresKeyboard)
        log.debug "Return features menu to admin"
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = SWITCH_FEATURE)
    switchFeature(String configKey) {
        setConfig(configKey, (!isEnabled(configKey)).toString())
        log.debug 'Switching config with key {}', configKey
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = SET_LOGGING)
    setLoggingChat() {
        if (currentChat.type == ChatType.PRIVATE) {
            sendMessage.text('text.error.chat.set.logging')
        } else {
            unsetOldChat()
            setNewChat()
            sendMessage.text('text.chat.set.logging')
        }
    }

    @Action(skip = true)
    @Secured(roles = Role.LOGGING_CHAT)
    @BotRequest()
    checkMessage() {
        if (isEnabled(CHECK_MESSAGES_IN_LOGGING_CHAT)) {
            def message = updateService.getMessageFromUpdate(update).strip()
            def resultMessage = message.tokenize(' ')
                    .collect { it.startsWith('\n') ? ['\n', it.substring('\n'.size())] : it }
                    .flatten()
                    .collect { it.startsWith('\\$') && it.endsWithIgnoreCase('id') ? '$' + '' : it }
                    .collect { it.startsWith('$') ? it.bold() : it }
                    .join(' ')
            resultMessage = resultMessage.replaceAll ('\n ', '\n')
            resultMessage = resultMessage.replaceAll('\\$', '')
            sendMessage.text resultMessage
        }
    }

    private void setNewChat() {
        currentChat.setRole(ChatRole.LOGGING_CHAT)
        userService.updateChat(currentChat)
    }

    private void unsetOldChat() {
        def oldLoggingChat = userService.getChatForRole(ChatRole.LOGGING_CHAT)
        if (oldLoggingChat) {
            oldLoggingChat.setRole(ChatRole.NONE)
            userService.updateChat(oldLoggingChat)
        }
    }
}
