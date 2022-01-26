package com.khabaznia.bot.trait

import com.khabaznia.bot.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
trait Localized {

    @Autowired
    private I18nService i18nService

    String getText(String key, Map binding) {
        i18nService.getFilledTemplate(key, binding)
    }

    String getText(String key) {
        i18nService.getMessage(key)
    }
}