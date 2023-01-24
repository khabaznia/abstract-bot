package com.khabaznia.bots.core.util

import org.apache.groovy.parser.antlr4.util.StringUtils

import static com.khabaznia.bots.core.routing.Constants.*

class HTMLParsingUtil {

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

    static String makeTextAsSpoiler(Object key) {
        key instanceof String ? "<tg-spoiler>$key</tg-spoiler>" : null
    }

    static String userMention(Object key) {
        key instanceof String ? "tg://user?id=$key" : null
    }

    static String linkText(Object url, String text) {
        url instanceof String ? "<a href=\"$url\">$text</a>" : null
    }

    static String linkUrl(Object text, String url) {
        text instanceof String ? "<a href=\"$url\">$text</a>" : null
    }

    static String mapHTMLParsableSymbols(String input) {
        def mappingsSymbols =
                ["<"   : '',
                 ">"   : '',
                 "&"   : '&amp;',
                 "\'"  : '&quot;',
                 '\\$' : '',
                 '\\\\': '/',
                 "\""  : '&quot;']
        def result = input
        mappingsSymbols.each { result = result.replaceAll(it.key, it.value) }
        result
    }

    static String mapReverseHTMLParsableSymbols(String input) {
        def reverseMappingsSymbols =
                ['&amp;' : '&',
                 '&quot;': "\'"]
        def result = input
        reverseMappingsSymbols.each { result = result.replaceAll(it.key, it.value) }
        result
    }


    static String concat(Object result, String value, String separator = ',', String prefix = '') {
        if (!(result instanceof String)) {
            return null
        }
        result = result.concat(StringUtils.isEmpty(value) ? '' : "$separator $prefix$value ")
        result
    }
}
