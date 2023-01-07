package com.khabaznia.bots.common.controller.common

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bots.core.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.common.Constants.LANG_CONTROLLER.*
import static com.khabaznia.bots.core.routing.Constants.*
import static com.khabaznia.bots.core.meta.Emoji.FINGER_DOWN
import static com.khabaznia.bots.core.util.SessionUtil.currentChat

@Slf4j
@Component
@BotController
class LangController extends AbstractBotController {

    @Autowired
    private I18nService i18nService

    @BotRequest(path = CHANGE_LANG_AS_COMMAND)
    displayLangCommand() {
        displayLang()
    }

    @Localized
    @BotRequest(path = DISPLAY_CHANGE_LANG)
    displayLang() {
        if (!canChangeLang()) return TO_MAIN
        sendMessage.text('text.select.lang')
                .emoji(FINGER_DOWN)
                .keyboard(localeKeyboard)
        log.debug 'Default lang menu'
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_EN)
    String changeLocaleEn() {
        if (!canChangeLang()) return TO_MAIN
        i18nService.changeLocale('en')
        log.debug 'Change locale to {} for chat {}', 'en', currentChat?.code
        TO_MAIN
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_UK)
    String changeLocaleUk() {
        if (!canChangeLang()) return TO_MAIN
        i18nService.changeLocale('uk')
        log.debug 'Change locale to {} for chat {}', 'uk', currentChat?.code
        TO_MAIN
    }

    @Localized
    @BotRequest(path = CHANGE_LANG_RU)
    String changeLocaleRu() {
        if (!canChangeLang()) return TO_MAIN
        i18nService.changeLocale('ru')
        log.debug 'Change locale to {} for chat {}', 'ru', currentChat?.code
        TO_MAIN
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

    private boolean canChangeLang() {
        if (isEnabled(USE_ONLY_DEFAULT_LANGUAGE)) {
            sendMessage.text('text.lang.only.one.lang.is.available.for.now')
                    .binding([lang: LANG_EMOJI.get(getConfig(DEFAULT_LOCALE))])
            return false
        }
        if (currentChat.type == ChatType.GROUP && isEnabled(DEFAULT_LANGUAGE_FOR_GROUPS)) return false
        true
    }
}
