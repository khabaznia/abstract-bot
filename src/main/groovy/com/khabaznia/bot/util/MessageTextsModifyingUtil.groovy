package com.khabaznia.bot.util

import static com.khabaznia.bot.core.Constants.*

class MessageTextsModifyingUtil {

    static String addParamsToCallbackData(Object callbackData, Map<String, String> params) {
        if (!(callbackData instanceof String)) {
            return null
        }
        def paramsAsString = params.collect { it.key + PARAMETER_KEY_VALUE_DELIMITER + it.value }
                .join(PARAMETERS_DELIMITER)
        def pathWithPrefix = callbackData.contains(PARAMETERS_PREFIX)
                ? callbackData + PARAMETERS_DELIMITER
                : callbackData + PARAMETERS_PREFIX
        return pathWithPrefix + paramsAsString
    }

    static String addEmojiToKeyMessage(Object key, String emoji) {
        if (!(key instanceof String)) {
            return null
        }
        return (key.contains(PARAMETERS_PREFIX) ? key + " " : key + PARAMETERS_PREFIX).concat(emoji)
    }
}
