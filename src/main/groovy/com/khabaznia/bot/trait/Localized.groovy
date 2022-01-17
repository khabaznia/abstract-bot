package com.khabaznia.bot.trait

import com.khabaznia.bot.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
trait Localized {

    @Autowired
    I18nService i18nService

    String getText(final String key, final Map binding) {
        i18nService.getFilledTemplate(key, binding)
    }

    String getText(final String key) {
        i18nService.getMessage(key)
    }
}