package com.khabaznia.bot.service

import com.khabaznia.bot.repository.ChatRepository
import com.khabaznia.bot.strategy.impl.ResourceMediaFileRetrievingStrategy
import com.khabaznia.bot.trait.Configurable
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.core.Constants.*
import static com.khabaznia.bot.util.SessionUtil.getCurrentChat

@Slf4j
@Component
class I18nService implements Configurable {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ChatRepository chatRepository
    @Autowired
    private ResourceMediaFileRetrievingStrategy resourceMediaFileRetrievingStrategy

    private def markdownTextMap = [(/<b>.*<\/b>/): { it.bold() },
                                   (/<i>.*<\/i>/): { it.italic() },
                                   (/<u>.*<\/u>/): { it.underline() },
                                   (/<s>.*<\/s>/): { it.strikethrough() },
    ]

    String getText(String mediaName, String lang = null) {
        resourceMediaFileRetrievingStrategy.getMediaForCode('text/' + mediaName + '_' + getLang(lang) + '.txt').text
    }

    boolean changeLocale(String localeKey) {
        log.trace 'Try to change locale to -> {}', localeKey
        if (localeKey && getConfigs(AVAILABLE_LOCALES).contains(localeKey)) {
            currentChat.lang = localeKey
            chatRepository.save(currentChat)
            log.info 'Locale for chat {} changed to -> {}', currentChat.code, localeKey
            return true
        }
        return false
    }

    void setUserLang(Update update) {
        if (!currentChat.lang) {
            if (!changeLocale(UpdateService.getMessage(update)?.from?.languageCode)) {
                changeLocale(getConfig(DEFAULT_LOCALE))
            }
        }
    }

    String getLang(String lang) {
        if (isEnabled(USE_ONLY_DEFAULT_LANGUAGE))
            return getConfig(DEFAULT_LOCALE)
        lang ?: currentChat?.lang ?: getConfig(DEFAULT_LOCALE)
    }

    String getFilledTemplate(String stringTemplateKey, Map<String, String> binding = [:], String locale = null) {
        def localizedMessage = getMessage(stringTemplateKey, locale)
        try {
            def engine = new GStringTemplateEngine()
            def template = engine.createTemplate(localizedMessage).make(binding)
            return template as String
        } catch (Exception ex) {
            log.warn ex.message
            return "BROKEN MESSAGE. FIX ME:\n $localizedMessage"
        }
    }

    String getFilledTemplateWithEmoji(String stringTemplateKey, Map<String, String> binding, String emoji, String locale = null) {
        emoji
                ? getFilledTemplate(stringTemplateKey, binding, locale) + " " + emoji
                : getFilledTemplate(stringTemplateKey, binding, locale)
    }

    private String getMessage(String key, String locale = null) {
        if (key) {
            boolean hasMarkdown = key.matches(/<[bius]>.*<\/[bius]>/)
            def markdownMethod = hasMarkdown
                    ? markdownTextMap.find { key.matches(it.key) }.value
                    : null
            def keyWithoutMarkdown = hasMarkdown ? key - ~/<[bius]>/ - ~/<\/[bius]>/ : key
            def localizedMessage = getLocalized(keyWithoutMarkdown, locale)
            return hasMarkdown ? markdownMethod(localizedMessage) : localizedMessage
        }
        ''
    }

    private String getLocalized(String key, String locale) {
        def messageLocale = getLang(locale)
        def localeFromChat = new Locale(messageLocale)
        context.getMessage(key, null, localeFromChat)
    }
}
