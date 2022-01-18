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

    InlineKeyboardMarkup toApiKeyboard(InlineKeyboard keyboard) {
        log.trace 'Map inline keyboard: {}', keyboard
        def result = new InlineKeyboardMarkup()
        result.setKeyboard(keyboard.get().collect {
            it.collect {
                new InlineKeyboardButton(
                        text: i18nService.getFilledTemplate(it.key, it.binding, it.emoji),
                        callbackData: it.callbackData)
            }
        })
        log.trace 'After mapping. Markup: {}', result
        result
    }

    ReplyKeyboardMarkup toApiKeyboard(ReplyKeyboard keyboard) {
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
        log.trace 'Map inline keyboard to model: {}', keyboardMarkup
        def result = new Keyboard(type: KeyboardType.INLINE,
                rows: keyboardMarkup.keyboard.collect {
                    new Row(buttons: it.collectEntries { [it.callbackData, it.text] } as Map<String, String>)
                })
        log.trace 'After mapping. Model: {}', result
        result
    }

    Keyboard toKeyboardModel(ReplyKeyboardMarkup keyboardMarkup) {
        log.trace 'Map reply keyboard to model: {}', keyboardMarkup
        def result = new Keyboard(type: KeyboardType.INLINE,
                rows: keyboardMarkup.keyboard.collect {
                    new Row(buttons: it.collectEntries { [it.text, it.text] } as Map<String, String>)
                })
        log.trace 'After mapping. Model: {}', result
        result
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
