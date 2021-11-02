package com.khabaznia.bot.controller.settings

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SETTINGS_CONTROLLER.*

@Slf4j
@Component
@BotController(path = SETTINGS_CONTROLLER)
class SettingsController extends AbstractBotController {

    @Localized
    @BotRequest
    getSettings() {

    }

    @Localized
    @BotRequest(path = CHANGE_LANG_SETTING)
    String changeLang() {

    }

    @Localized
    @BotRequest(path = BACK_BUTTON)
    String back() {

    }
}
