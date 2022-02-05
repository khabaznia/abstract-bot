package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.enums.KeyboardType
import com.khabaznia.bot.meta.keyboard.Button
import com.khabaznia.bot.meta.keyboard.impl.InlineButton
import com.khabaznia.bot.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bot.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bot.model.Keyboard
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

@Slf4j
@Component
class KeyboardMapper {

    @Autowired
    private PathCryptService pathCryptService
    @Autowired
    private I18nService i18nService
    @Autowired
    private ApplicationContext context

    org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard toApiKeyboard(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        keyboard ?
                keyboard instanceof InlineKeyboard
                        ? toInlineApiKeyboard(keyboard)
                        : keyboard instanceof ReplyKeyboard ? toReplyApiKeyboard(keyboard) : null
                : null
    }

    InlineKeyboardMarkup toInlineApiKeyboard(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        if (keyboard instanceof InlineKeyboard) {
            def result = new InlineKeyboardMarkup()
            result.setKeyboard(keyboard.get().collect {
                it.each { it.params.putAll(keyboard.getKeyboardParams()) }
                        .collect {
                            new InlineKeyboardButton(
                                    text: getButtonText(it),
                                    callbackData: getCallBackData(it))
                        }
            })
            return result
        }
        null
    }

    private ReplyKeyboardMarkup toReplyApiKeyboard(ReplyKeyboard keyboard) {
        def result = new ReplyKeyboardMarkup()
        result.setKeyboard(keyboard.get().collect {
            def row = new KeyboardRow()
            row.addAll(it.collect { getButtonText(it) })
            row
        })
        result.setResizeKeyboard(true)
        result.setOneTimeKeyboard(false)
        result.setInputFieldPlaceholder(i18nService.getFilledTemplate('reply.keyboard.placeholder', [:]))
        result
    }

    private String getCallBackData(InlineButton button) {
        pathCryptService.encryptPath(button.params.isEmpty() ? button.callbackData : button.callbackData.addParams(button.params))
    }

    private String getButtonText(Button button) {
        i18nService.getFilledTemplate(button.key, button.binding, button.emoji)
    }

    static Keyboard toKeyboardModel(com.khabaznia.bot.meta.keyboard.Keyboard keyboard) {
        def rowPosition = 0
        !keyboard ? null :
        new Keyboard(buttons: keyboard.get().collect {
            def buttonPosition = 0
            def rowButtons = it.each {
                if (it instanceof InlineButton) {
                    it.params.putAll(keyboard.getKeyboardParams())
                }
            }
                    .collect { convertToButtonModel((Button) it, rowPosition, buttonPosition++) }
            rowPosition++
            rowButtons
            }.flatten(),
                type: keyboard instanceof InlineKeyboard ? KeyboardType.INLINE : KeyboardType.REPLY)
    }

    static InlineKeyboard fromKeyboardModel(Keyboard keyboard) {
        def result = !keyboard.buttons.isEmpty()
                ? new InlineKeyboard().setRows(
                keyboard.buttons
                        .groupBy { it.rowPosition }
                        .sort { it.key }
                        .collect {
                            it.value
                                    .sort { it.position }
                                    .collect { convertButton(it) }
                        }) as InlineKeyboard
                : null
        result
    }

    static com.khabaznia.bot.model.Button convertToButtonModel(Button source, Integer rowPosition, Integer buttonPosition) {
        new com.khabaznia.bot.model.Button(
                id: source.id,
                key: source.key,
                emoji: source.emoji,
                binding: source.binding,
                callbackData: source instanceof InlineButton ? source.callbackData : '',
                params: source instanceof InlineButton ? source.params : [:],
                type: source.type,
                position: buttonPosition,
                rowPosition: rowPosition)
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
