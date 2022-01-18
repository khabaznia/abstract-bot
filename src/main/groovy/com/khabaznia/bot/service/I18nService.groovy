package com.khabaznia.bot.service

import com.khabaznia.bot.repository.ChatRepository
import com.khabaznia.bot.trait.Configured
import com.khabaznia.bot.util.SessionUtil
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import static com.khabaznia.bot.configuration.CustomLocaleResolver.AVAILABLE_LOCALES
import static com.khabaznia.bot.configuration.CustomLocaleResolver.DEFAULT_LOCALE

@Slf4j
@Component
class I18nService implements Configured {

    @Autowired
    ApplicationContext context
    @Autowired
    ChatRepository chatRepository

    boolean changeLocale(String localeKey) {
        log.debug 'Try to change locale to -> {}', localeKey
        if (localeKey && getConfigs(AVAILABLE_LOCALES).contains(localeKey)) {
            def chatToUpdate = SessionUtil.currentChat
            chatToUpdate.lang = localeKey
            chatRepository.save(chatToUpdate)
            log.info 'Locale for chat {} changed to -> {}', chatToUpdate.code, localeKey
            return true
        }
        return false
    }

    String getMessage(String key) {
        def localeFromChat = new Locale(SessionUtil.currentChat?.lang ?: DEFAULT_LOCALE)
        context.getMessage(key, null, localeFromChat)
    }

    String getFilledTemplate(String stringTemplateKey, Map<String, String> binding) {
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(getMessage(stringTemplateKey)).make(binding)
        template as String
    }

    String getFilledTemplate(String stringTemplateKey, Map<String, String> binding, String emoji) {
        emoji
                ? getFilledTemplate(stringTemplateKey, binding) + " " + emoji
                : getFilledTemplate(stringTemplateKey, binding)
    }
}
