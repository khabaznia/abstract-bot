package com.khabaznia.bot.service

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.core.Constants.PARAMETERS_PREFIX
import static com.khabaznia.bot.core.Constants.PARAMETERS_DELIMITER
import static com.khabaznia.bot.core.Constants.PARAMETER_KEY_VALUE_DELIMITER
import static com.khabaznia.bot.security.Constants.CHAT_ID_DELIMITER

@Slf4j
@Service
class UpdateService {

    @Autowired
    PathCryptService pathCryptService

    static String getChatInfoFromUpdate(Update update) {
        def message = getMessage(update)
        if (message) {
            return message?.chat?.id + CHAT_ID_DELIMITER + message?.from?.id
        } else {
            return update.myChatMember?.chat?.id + CHAT_ID_DELIMITER + update.myChatMember?.from?.id
        }
    }


    String getMessageFromUpdate(Update update) {
        def message = update.hasCallbackQuery() ? update?.callbackQuery?.data : getMessage(update)?.text
        pathCryptService.isEncrypted(message) ? pathCryptService.decryptPath(message) : message ?: ''
    }

    Map<String, String> getParametersFromUpdate(Update update) {
        def message = getMessageFromUpdate(update)
        message?.contains(PARAMETERS_PREFIX)
                ? getParametersFromMessage(message)
                : [:]
    }

    private static Message getMessage(Update update) {
        if (update.hasMessage())
            return update?.message
        if (update.hasCallbackQuery())
            return update?.callbackQuery?.message
        if (update.hasEditedMessage())
            return update?.editedMessage
        return null
    }

    static Map<String, String> getParametersFromMessage(String message) {
        message?.tokenize(PARAMETERS_PREFIX)[1]?.tokenize(PARAMETERS_DELIMITER)
                ?.collectEntries { [it.tokenize(PARAMETER_KEY_VALUE_DELIMITER)[0], it.tokenize(PARAMETER_KEY_VALUE_DELIMITER)[1]] }
    }
}
