package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.enums.Role
import com.khabaznia.bot.util.DefaultRoleKeyboardsUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.meta.Emoji.*
import static com.khabaznia.bot.util.SessionUtil.getCurrentUser

@Slf4j
@Component
@BotController
class SettingsController extends AbstractBotController {

    @Autowired
    private DefaultRoleKeyboardsUtil keyboardsUtil

    @Localized
    @BotRequest(path = SETTINGS)
    getSettings() {
        sendMessage
                .text('text.settings.menu')
                .emoji(FINGER_DOWN)
                .keyboard(keyboardsUtil.settingsKeyboard)
        log.debug 'Default settings menu'
    }

    @Localized
    @Secured(roles = Role.USER)
    @BotRequest(path = GENERAL_SUBSCRIPTION_NOTIFY_ALL)
    switchSubscription() {
        currentUser.subscription.general = !currentUser.subscription.general
        userService.updateUser(currentUser)
        sendMessage.text('text.settings.subscriptions.updated')
                .keyboard(keyboardsUtil.settingsKeyboard)
    }
}
