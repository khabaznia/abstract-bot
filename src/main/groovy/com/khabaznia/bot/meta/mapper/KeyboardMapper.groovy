package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.meta.Emoji
import com.khabaznia.bot.meta.keyboard.Button
import com.khabaznia.bot.meta.keyboard.impl.InlineButton
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard

import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard

import com.khabaznia.bot.model.Keyboard
import com.khabaznia.bot.model.Row
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.service.PathCryptService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

import javax.annotation.PostConstruct

@Slf4j
@Component
class KeyboardMapper {

//    List<String> emojiList

    @Autowired
    PathCryptService pathCryptService
    @Autowired
    I18nService i18nService
    @Autowired
    ApplicationContext context

//    @PostConstruct
//    void setUpEmojiList() {
//        Emoji.declaredFields.findAll { java.lang.reflect.Modifier.isStatic(it.getModifiers()) }
//                .each { it.setAccessible(true) }
//                .collect { it.get(com.khabaznia.bot.meta.Emoji.class) as java.lang.String }
//                .findAll { it != null }
//                .findAll { !it.matches(/[a-zA-Z]*/) }
//        log.trace 'Emoji list: {}', emojiList
//    }

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
                                    text: getButtonText(it),
                                    callbackData: getCallBackData(it))
                        }
            })
            log.trace 'After mapping. Markup: {}', result
            return result
        }
        null
    }

    private ReplyKeyboardMarkup toReplyApiKeyboard(ReplyKeyboard keyboard) {
        log.trace 'Map reply keyboard: {}', keyboard
        def result = new ReplyKeyboardMarkup()
        result.setKeyboard(keyboard.get().collect {
            def row = new KeyboardRow()
            row.addAll(it.collect { getButtonText(it) })
            row
        })
        log.trace 'After mapping. Markup: {}', result
        result
    }

    private String getCallBackData(InlineButton button) {
        pathCryptService.encryptPath(button.params.isEmpty() ? button.callbackData : button.callbackData.addParams(button.params))
    }

    private String getButtonText(Button button) {
        i18nService.getFilledTemplate(button.key, button.binding, button.emoji)
    }

    static Keyboard toKeyboardModel(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        log.trace 'Map keyboard from obj to model: {}', keyboard
        def result = keyboard
                ? new Keyboard(rows: keyboard.get().collect {
                    new Row(buttons: it.collect { convertToButtonModel(it as Button) })
                })
                : null
        log.trace 'After mapping. Model: {}', result
        result
    }

    static InlineKeyboard fromKeyboardModel(Keyboard keyboard) {
        log.trace 'Map keyboard from model to object: {}', keyboard
        def result = keyboard
                ? new InlineKeyboard().setRows(
                    keyboard.rows.collect { it.buttons.collect { convertButton(it) } }) as InlineKeyboard
                : null
        log.trace 'After mapping. Model: {}', result
        result
    }

    static com.khabaznia.bot.model.Button convertToButtonModel(Button source) {
        new com.khabaznia.bot.model.Button(
                id: source.id,
                key: source.key,
                emoji: source.emoji,
                binding: source.binding,
                callbackData: source instanceof InlineButton ? source.callbackData : '',
                params: source instanceof InlineButton ? source.params : [:],
                type: source.type)
    }

    static InlineButton convertButton(com.khabaznia.bot.model.Button source) {
        new InlineButton(
                params: source.params,
                callbackData: source.callbackData,
                key: source.key,
                emoji: source.emoji,
                binding: source.binding,
                type: source.type,
                id: source.id)
    }
}
