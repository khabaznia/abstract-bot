package com.khabaznia.bot.controller.admin

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.enums.Role
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.COMMON.SETTINGS
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.EXAMPLE
import static com.khabaznia.bot.enums.MessageType.INLINE_KEYBOARD_MESSAGE_GROUP
import static com.khabaznia.bot.meta.Emoji.*

@Slf4j
@Component
@BotController
class AdminController extends AbstractBotController {

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = ADMIN_START)
    String onStart() {
        sendMessage.text('message.admin.greeting').emoji(CROWN).type(MessageType.DELETE)
        ADMIN_TO_MAIN
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = ADMIN_TO_MAIN)
    adminMenu() {
        deleteOldMessages(INLINE_KEYBOARD_MESSAGE_GROUP)
        sendMessage.text('message.choose.action')
                .emoji(FINGER_DOWN)
                .replyKeyboard([[EXAMPLE.addEmoji(MEDITATE)], [FEATURES_LIST.addEmoji(CHECK)], [SETTINGS.addEmoji(GEAR)]])
    }

    @Secured(roles = Role.ADMIN)
    @Localized
    @BotRequest(path = FEATURES_LIST)
    getFeatures() {
        def featuresKeyboard = inlineKeyboard
        switchableConfigs.each { featuresKeyboard.switchButton(it.name, SWITCH_FEATURE, Boolean.valueOf(it.value), [configKey: it.key]).row() }
        featuresKeyboard.button('button.back', LEFT_ARROW, ADMIN_TO_MAIN)
        sendMessage.text('message.admin.switch.features')
                .keyboard(featuresKeyboard)
        log.debug "Return features menu to admin"
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = SWITCH_FEATURE)
    switchFeature(String configKey) {
        setConfig(configKey, (!isEnabled(configKey)).toString())
        log.debug 'Switching config with key {}', configKey
    }
}
