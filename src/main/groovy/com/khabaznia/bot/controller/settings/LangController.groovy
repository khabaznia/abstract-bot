package com.khabaznia.bot.controller.settings

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.LANG_CONTROLLER.*

@Slf4j
@Component
@BotController(path = LANG_CONTROLLER)
class LangController extends AbstractBotController {

    @Autowired
    I18nService i18nService

    @BotRequest(path = DISPLAY_LANG)
    displayLang() {

    }

    @Localized
    @BotRequest(path = CHANGE_LANG)
    String changeLocale(final Map<String, String> params) {

    }
}
