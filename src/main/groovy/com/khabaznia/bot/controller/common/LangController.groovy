package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.LANG_CONTROLLER.*
import static com.khabaznia.bot.core.Constants.AVAILABLE_LOCALES
import static com.khabaznia.bot.meta.Emoji.FINGER_DOWN

@Slf4j
@Component
@BotController
class LangController extends AbstractBotController {

    @Autowired
    private I18nService i18nService

    @Localized
    @BotRequest(path = DISPLAY_CHANGE_LANG)
    displayLang() {
        sendMessage.text('message.select.lang')
                .emoji(FINGER_DOWN)
                .keyboard(localeKeyboard)
        log.debug 'Default lang menu'
    }

    private ReplyKeyboard getLocaleKeyboard() {
        def result = replyKeyboard
        localeButtons.each { result.button(it.key, it.value) }
        result
    }

    private Map<String, String> getLocaleButtons() {
        getConfigs(AVAILABLE_LOCALES)
                .collectEntries({ [(CHANGE_LANG.concat('.').concat(it)): LANG_EMOJI[it]] })
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_EN)
    String changeLocaleEn() {
        i18nService.changeLocale('en')
        log.debug 'Change locale to {} for chat {}', 'en', SessionUtil.currentChat?.code
        TO_MAIN
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_UK)
    String changeLocaleUk() {
        i18nService.changeLocale('uk')
        log.debug 'Change locale to {} for chat {}', 'uk', SessionUtil.currentChat?.code
        TO_MAIN
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_RU)
    String changeLocaleRu() {
        i18nService.changeLocale('ru')
        log.debug 'Change locale to {} for chat {}', 'ru', SessionUtil.currentChat?.code
        TO_MAIN
    }
}
