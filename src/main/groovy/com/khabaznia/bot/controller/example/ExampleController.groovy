package com.khabaznia.bot.controller.example

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*

@Slf4j
@Component
@BotController()
class ExampleController extends AbstractBotController {

    @Localized
    @BotRequest(path = EXAMPLE)
    getReply() {
        sendMessage
                .key('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, ACTION_TWO, ONE_TIME_INLINE_KEYBOARD], [TO_MAIN]])
        sendMessage.key(NEXT)
    }

    @BotRequest(path = NEXT, after = EXAMPLE)
    getNext() {
        sendMessage
                .key('Another reply keyboard')
                .replyKeyboard([[ACTION_TWO, MODIFIABLE_INLINE_KEYBOARD],
                                [ONE_TIME_INLINE_KEYBOARD]])
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.key('action two')
                .keyboard(inlineKeyboard
                        .button('example.simple.button', '/query')
                        .button('example.emoji.button', Emoji.AVOCADO, '/query')
                        .buttonWithBinding('example.binding.button', '/query', [binding: 'some'])
                        .row()
                        .oneTimeButton('OT button', '/query')
                        .oneTimeButton('OT emoji', Emoji.BOAT, '/query')
                        .oneTimeButton('OT param', '/queryWithParam', [someUniqueId: '34'])
                        .row()
                        .switchButton('SB with param', '/queryWithParam', true, [someUniqueId: '34']))
    }

    @Localized
    @BotRequest(path = ACTION_TWO)
    actionTwo() {
        sendMessage.key('Localized ')
                .inlineKeyboard([['example.button.one': "/query", 'example.button.two': "/query"],
                                 [('button.example.back'): BACK_ACTION]])
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.key('<b>query</b> - ok').type(MessageType.DELETE)
    }

    @BotRequest(path = '/queryWithParam')
    query(String someUniqueId) {
        sendMessage.key('query - <i>ok</i>').type(MessageType.DELETE)
        sendMessage.key(someUniqueId).type(MessageType.DELETE)
    }

    @Localized
    @BotRequest(path = ONE_TIME_INLINE_KEYBOARD)
    getInline() {
        sendMessage
                .key('Main menu')
                .keyboard(inlineKeyboard.button('Yes, I confirm', YES_ACTION)
                        .button("No, don't confirm", NO_ACTION, [param: 'some_param', other: 'other_param'])
                        .row()
                        .button('button.example.back', BACK_ACTION)
                )
                .type(MessageType.ONE_TIME_INLINE_KEYBOARD)
    }

    @BotRequest(path = YES_ACTION)
    yesAction() {
        sendMessage.key('yes')
    }

    @BotRequest(path = NO_ACTION)
    noAction(String param, String other) {
        sendMessage.key('no')
        sendMessage.key(param).type(MessageType.DELETE)
        sendMessage.key(other).type(MessageType.DELETE)
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.key('back')
        EXAMPLE
    }
}
