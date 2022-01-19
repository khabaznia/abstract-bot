package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.enums.KeyboardType
import com.khabaznia.bot.meta.keyboard.impl.InlineButton
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyButton
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.model.Keyboard
import com.khabaznia.bot.model.Row
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.service.PathCryptService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

import static com.khabaznia.bot.core.Constants.PARAMETERS_PREFIX
import static com.khabaznia.bot.service.UpdateService.getParametersFromMessage

@Slf4j
@Component
class KeyboardMapper {

    @Autowired
    PathCryptService pathCryptService
    @Autowired
    I18nService i18nService

    org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard toApiKeyboard(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        keyboard ?
                keyboard instanceof InlineKeyboard
                        ? toInlineApiKeyboard(keyboard)
                        : keyboard instanceof ReplyKeyboard ? toReplyApiKeyboard(keyboard) : null
                : null
    }

    InlineKeyboardMarkup toInlineApiKeyboard(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        if (keyboard instanceof InlineKeyboard) {
            log.trace 'Map inline keyboard: {}', keyboard
            def result = new InlineKeyboardMarkup()
            result.setKeyboard(keyboard.get().collect {
                it.each { it.params.putAll(keyboard.getKeyboardParams()) }
                        .collect {
                            new InlineKeyboardButton(
                                    text: i18nService.getFilledTemplate(it.key, it.binding, it.emoji),
                                    callbackData: getCallBackData(it))
                        }
            })
            log.trace 'After mapping. Markup: {}', result
            return result
        }
        null
    }

    private String getCallBackData(InlineButton button) {
        pathCryptService.encryptPath(button.params.isEmpty() ? button.callbackData : button.callbackData.addParams(button.params))
    }

    private ReplyKeyboardMarkup toReplyApiKeyboard(ReplyKeyboard keyboard) {
        log.trace 'Map reply keyboard: {}', keyboard
        def result = new ReplyKeyboardMarkup()
        result.setKeyboard(keyboard.get().collect {
            def row = new KeyboardRow()
            row.addAll(it.collect { i18nService.getFilledTemplate(it.key, it.binding, it.emoji) })
            row
        })
        log.trace 'After mapping. Markup: {}', result
        result
    }

    Keyboard toKeyboardModel(InlineKeyboardMarkup keyboardMarkup) {
        if (keyboardMarkup) {
            log.trace 'Map inline keyboard to model: {}', keyboardMarkup
            def result = new Keyboard(type: KeyboardType.INLINE,
                    rows: keyboardMarkup?.keyboard?.collect {
                        new Row(buttons: it.collectEntries { [pathCryptService.decryptPath(it.callbackData), it.text] } as Map<String, String>)
                    })
            log.trace 'After mapping. Model: {}', result
            return result
        }
        return null
    }

    com.khabaznia.bot.meta.keyboard.Keyboard keyboardFromModel(Keyboard source) {
        log.trace 'Map keyboard from model: {}', source
        def result = source.type == KeyboardType.INLINE
                ? getInlineKeyboard(source.rows)
                : getReplyKeyboard(source.rows)
        log.trace 'After mapping. Model: {}', result
        result
    }

    private static InlineKeyboard getInlineKeyboard(List<Row> rows) {
        new InlineKeyboard(rows:
                rows.collect {
                    it.buttons.collect {
                        new InlineButton(key: it.key,
                                callbackData: it.value?.split(PARAMETERS_PREFIX)[0],
                                params: getParametersFromMessage(it.value))
                    }
                })
    }

    private static ReplyKeyboard getReplyKeyboard(List<Row> rows) {
        new ReplyKeyboard(rows:
                rows.collect {
                    it.buttons.collect {
                        new ReplyButton(key: it.key)
                    }
                })
    }
}
