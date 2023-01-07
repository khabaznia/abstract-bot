package com.khabaznia.bots.common.controller.common

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import com.khabaznia.bots.core.routing.annotation.Secured
import com.khabaznia.bots.core.enums.Role
import com.khabaznia.bots.common.util.DefaultRoleKeyboardsUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.COMMON.*
import static com.khabaznia.bots.core.meta.Emoji.*
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentUser

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
