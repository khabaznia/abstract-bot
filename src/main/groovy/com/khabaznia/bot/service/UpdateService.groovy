package com.khabaznia.bot.service

import groovy.util.logging.Slf4j
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

    String getChatInfoFromUpdate(Update update) {
        def message = getMessage(update)
        if (message) {
            return message?.chat?.id + CHAT_ID_DELIMITER + message?.from?.id
        } else {
            return update.myChatMember?.chat?.id + CHAT_ID_DELIMITER + update.myChatMember?.from?.id
        }
    }


    String getMessageFromUpdate(final Update update) {
        def message = update.hasCallbackQuery() ? update?.callbackQuery?.data : getMessage(update)?.text
//        pathCryptService.isEncrypted(message) ? pathCryptService.decryptPath(message) : message?:''
        message?:''
    }

    Map<String, String> getParametersFromUpdate(final Update update) {
        def message = getMessageFromUpdate(update)
        message?.contains(PARAMETERS_PREFIX)
                ? getParametersFromMessage(message)
                : [:]
    }

    private static Message getMessage(final Update update) {
        if (update.hasMessage())
            return update?.message
        if (update.hasCallbackQuery())
            return update?.callbackQuery?.message
        if (update.hasEditedMessage())
            return update?.editedMessage
        return null
    }

    private static Map<String, String> getParametersFromMessage(final String message) {
        message?.split(PARAMETERS_PREFIX)[1]?.split(PARAMETERS_DELIMITER)
                ?.collectEntries { [it.split(PARAMETER_KEY_VALUE_DELIMITER)[0], it.split(PARAMETER_KEY_VALUE_DELIMITER)[1]] }
    }
}
