package com.khabaznia.bots.core.meta.mapper

import com.khabaznia.bots.core.enums.KeyboardType
import com.khabaznia.bots.core.meta.keyboard.Button
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboardRemove
import com.khabaznia.bots.core.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bots.core.model.Keyboard
import com.khabaznia.bots.core.service.ChatService
import com.khabaznia.bots.core.service.I18nService
import com.khabaznia.bots.core.service.PathCryptService
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
    @Autowired
    private ChatService chatService

    org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard toApiKeyboard(AbstractKeyboardMessage request) {
        def keyboard = request.keyboard
        if (keyboard instanceof ReplyKeyboardRemove) return toReplyKeyboardRemoveApiKeyboard(keyboard)
        !keyboard ? null :
                keyboard instanceof InlineKeyboard
                        ? toInlineApiKeyboard(request)
                        : keyboard instanceof ReplyKeyboard ? toReplyApiKeyboard(request) : null
    }

    InlineKeyboardMarkup toInlineApiKeyboard(AbstractKeyboardMessage request) {
        def keyboard = request.keyboard
        def chatLang = chatService.getChatLang(request.chatId)
        if (keyboard instanceof InlineKeyboard) {
            def result = new InlineKeyboardMarkup()
            result.setKeyboard(keyboard.get().collect {
                it.findAll().each { it.params.putAll(keyboard.getKeyboardParams()) }
                        .collect {
                            new InlineKeyboardButton(
                                    text: getButtonText(it, chatLang),
                                    url: it.url,
                                    callbackData: getCallBackData(it))
                        }
            })
            return result
        }
        null
    }

    private ReplyKeyboardMarkup toReplyApiKeyboard(AbstractKeyboardMessage request) {
        def keyboard = request.keyboard
        def chatLang = chatService.getChatLang(request.chatId)
        def result = new ReplyKeyboardMarkup()
        result.setKeyboard(keyboard.get().collect {
            def row = new KeyboardRow()
            row.addAll(it.collect { getButtonText(it, chatLang) })
            row
        })
        result.setResizeKeyboard(true)
        result.setOneTimeKeyboard(false)
        result.setInputFieldPlaceholder(i18nService.getFilledTemplate('text.reply.keyboard.placeholder'))
        result
    }

    private org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove toReplyKeyboardRemoveApiKeyboard(ReplyKeyboardRemove keyboard) {
        def removeKeyboard = new org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove()
        removeKeyboard.setRemoveKeyboard(keyboard.removeKeyboard)
        removeKeyboard
    }

    private String getCallBackData(InlineButton button) {
        !button.callbackData ? null :
                pathCryptService.encryptPath(button.params.isEmpty() ? button.callbackData : button.callbackData.addParams(button.params))
    }

    private String getButtonText(Button button, String chatLang) {
        i18nService.getFilledTemplateWithEmoji(button.text, button.binding, button.emoji, chatLang)
    }

    static Keyboard toKeyboardModel(com.khabaznia.bots.core.meta.keyboard.Keyboard keyboard) {
        def rowPosition = 0
        !keyboard ? null :
                new Keyboard(buttons: keyboard.get().collect {
                    def buttonPosition = 0
                    def rowButtons = it.each {
                        if (it instanceof InlineButton) {
                            it.params.putAll(keyboard.getKeyboardParams())
                        }
                    }
                            .findAll().collect { convertToButtonModel((Button) it, rowPosition, buttonPosition++) }
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

    static com.khabaznia.bots.core.model.Button convertToButtonModel(Button source, Integer rowPosition, Integer buttonPosition) {
        new com.khabaznia.bots.core.model.Button(
                id: source.id,
                key: source.text,
                emoji: source.emoji,
                binding: source.binding,
                url: source instanceof InlineButton ? source.url : '',
                callbackData: source instanceof InlineButton ? source.callbackData : '',
                params: source instanceof InlineButton ? source.params : [:],
                type: source.type,
                position: buttonPosition,
                rowPosition: rowPosition)
    }

    static InlineButton convertButton(com.khabaznia.bots.core.model.Button source) {
        new InlineButton(
                params: source.params,
                callbackData: source.callbackData,
                text: source.key,
                emoji: source.emoji,
                binding: source.binding,
                type: source.type,
                url: source.url,
                id: source.id)
    }
}
