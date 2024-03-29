package com.khabaznia.bots.core.configuration

import com.khabaznia.bots.core.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver

import static com.khabaznia.bots.core.routing.Constants.DEFAULT_LOCALE

@Slf4j
@Configuration
class CustomLocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    public static final String ENCODING = 'UTF-8'
    public static final String BUNDLE_NAME = 'messages'

    @Autowired
    protected Environment env
    @Autowired
    protected I18nService i18nService

    @Bean
    LocaleResolver localeResolver() {
        new SessionLocaleResolver(defaultLocale: new Locale(env.getProperty(DEFAULT_LOCALE)))
    }

    @Bean
    MessageSource messageSource() {
        new ResourceBundleMessageSource(basename: BUNDLE_NAME, defaultEncoding: ENCODING, useCodeAsDefaultMessage: true)
    }
}
