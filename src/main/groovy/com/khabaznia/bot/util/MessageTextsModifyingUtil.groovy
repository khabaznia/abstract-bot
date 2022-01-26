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

    static String makeTextItalic(Object key) {
        key instanceof String ? "<i>$key</i>" : null
    }

    static String makeTextBold(Object key) {
        key instanceof String ? "<b>$key</b>" : null
    }

    static String makeTextUnderline(Object key) {
        key instanceof String ? "<u>$key</u>" : null
    }

    static String makeTextStrikethrough(Object key) {
        key instanceof String ? "<s>$key</s>" : null
    }
}
