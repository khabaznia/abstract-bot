package com.khabaznia.bot.service

import com.khabaznia.bot.enums.Scope
import com.khabaznia.bot.meta.object.BotCommand
import com.khabaznia.bot.meta.object.BotCommandScope
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.SetMyCommands
import com.khabaznia.bot.trait.BaseRequests
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.khabaznia.bot.core.Constants.*
import static com.khabaznia.bot.controller.Constants.MAPPINGS.*

@Slf4j
@Service
class BotCommandService implements BaseRequests, Configurable {

    List<BaseRequest> getAdminAllGroupChatsCommands() {
        convertToSetMyCommands([(botCommandScope.type(Scope.ALL_CHAT_ADMINISTRATORS)): adminAllChatsCommands])
    }

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
