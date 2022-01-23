package com.khabaznia.bot.controller.sample

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER

@Slf4j
@Component
@BotController()
class SampleController extends AbstractBotController {

    @BotRequest(path = GET_REPLY)
    getReply() {
        sendMessage
                .key('hi')
                .keyboard([ACTION_ONE, ACTION_TWO, GET_INLINE])
                .type(MessageType.PERSIST)
    }

    @BotRequest(path = '/next', after = GET_REPLY)
    getNext() {
        sendMessage
                .key('hi')
                .replyKeyboard([[ACTION_TWO, ACTION_ONE],
                                [GET_INLINE]])
    }

    @Localized
    @BotRequest(path = ACTION_ONE)
    getFeatures() {
        sendMessage.key('action two')
                .keyboard(inlineKeyboard
                        .button('button', '/query')
                        .button('emoji', Emoji.AVOCADO, '/query')
                        .buttonWithBinding('With bind: $binding', '/query', [binding: 'some'])
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
        sendMessage.key('action two')
                .inlineKeyboard([[button1: "/query", button2: "/query"],
                                 [button3: "/query", button4: "/query"],
                                 [BACK: BACK_ACTION]])
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.key('<b>query</b> - ok')
    }

    @BotRequest(path = '/queryWithParam')
    query(String someUniqueId) {
        sendMessage.key('query - <i>ok</i>')
        sendMessage.key(someUniqueId).type(MessageType.DELETE)
    }

    @Localized
    @BotRequest(path = GET_INLINE)
    getInline() {
        sendMessage
                .key('Main menu')
                .keyboard(inlineKeyboard.button('yes, I confirm', YES_ACTION)
                        .button("no, don't confirm", NO_ACTION, [param: 'some_param', other: 'other_param'])
                        .row()
                        .button('Back', BACK_ACTION)
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
        GET_REPLY
    }
}
