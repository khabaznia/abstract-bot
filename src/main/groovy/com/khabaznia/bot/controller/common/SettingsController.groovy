package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.LANG_CONTROLLER.*
import static com.khabaznia.bot.meta.Emoji.FINGER_DOWN
import static com.khabaznia.bot.meta.Emoji.LEFT_ARROW

@Slf4j
@Component
@BotController
class SettingsController extends AbstractBotController {

    @Localized
    @BotRequest(path = SETTINGS)
    getSettings() {
        sendMessage
                .text('message.settings.menu')
                .emoji(FINGER_DOWN)
                .keyboard(replyKeyboard
                        .button(DISPLAY_CHANGE_LANG, LANG_EMOJI.get(SessionUtil.currentChat.lang))
                        .row()
                        .button(TO_MAIN, LEFT_ARROW))
        log.debug 'Default settings menu'
    }
}
