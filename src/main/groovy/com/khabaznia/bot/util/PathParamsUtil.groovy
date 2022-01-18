package com.khabaznia.bot.util

import static com.khabaznia.bot.core.Constants.*

class PathParamsUtil {

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
}
