package com.khabaznia.bots.core.service


import com.khabaznia.bots.core.meta.object.BotCommand
import com.khabaznia.bots.core.meta.object.BotCommandScope
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.request.impl.SetMyCommands
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.khabaznia.bots.core.controller.Constants.MAPPINGS.langCommand
import static com.khabaznia.bots.core.routing.Constants.*

@Slf4j
@Service
class BotCommandService implements BaseRequests, Configurable {

    List<BaseRequest> convertToSetMyCommands(Map<BotCommandScope, Map<String, String>> commandsMap,
                                             boolean allLocalization = false,
                                             String defaultLang = getConfig(DEFAULT_LOCALE)) {
        commandsMap.collect {
            getCommandsForScope(it.value, it.key, allLocalization, defaultLang)
        }.flatten().findAll() as List<BaseRequest>
    }

    private List<BaseRequest> getCommandsForScope(Map<String, String> commandDescriptionMap,
                                                  BotCommandScope scope = botCommandScope,
                                                  boolean allLocalization, String defaultLang) {
        def defaultLangCommands =
                convertToRequest(scope, commandDescriptionMap, isEnabled(USE_ONLY_DEFAULT_LANGUAGE) ? getConfig(DEFAULT_LOCALE) : defaultLang)
                        .languageCode(null)
        def localizedCommands = isLocalizationAllowed(allLocalization)
                ? getConfigs(AVAILABLE_LOCALES).collect { lang -> convertToRequest(scope, withChangeLangCommand(commandDescriptionMap), lang) }
                : []
        localizedCommands + defaultLangCommands
    }

    private SetMyCommands convertToRequest(BotCommandScope scope, Map<String, String> commandDescriptionMap, String locale = null) {
        setMyCommands.commands(getCommandsForLocale(commandDescriptionMap, locale))
                .scope(scope)
                .languageCode(locale)
    }

    private List<BotCommand> getCommandsForLocale(Map<String, String> commandDescriptionMap, locale) {
        commandDescriptionMap.collect { command, descriptionKey ->
            botCommand.command(command)
                    .description(context.getMessage(descriptionKey, null, new Locale(locale)))
        }
    }

    private Map<String, String> withChangeLangCommand(Map<String, String> commandDescriptionMap) {
        commandDescriptionMap + langCommand
    }

    private boolean isLocalizationAllowed(boolean allLocalization) {
        if (!allLocalization) return false
        return !isEnabled(USE_ONLY_DEFAULT_LANGUAGE)
    }
}
