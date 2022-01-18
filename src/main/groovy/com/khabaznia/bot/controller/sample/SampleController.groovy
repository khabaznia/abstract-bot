package com.khabaznia.bot.controller.sample

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.SAMPLE_CONTROLLER

@Slf4j
@Component
@BotController(path = SAMPLE_CONTROLLER)
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
        sendMessage.key('action one')
                .keyboard(replyKeyboard.button(ACTION_TWO)
                        .row()
                        .button(SAMPLE_CONTROLLER + BACK_ACTION))
    }

    @Localized
    @BotRequest(path = ACTION_TWO)
    actionTwo() {
        sendMessage.key('action two')
                .inlineKeyboard([[button1: "/query", button2: "/query"],
                                 [button3: "/query", button4: "/query"],
                                 [BACK: SAMPLE_CONTROLLER + BACK_ACTION]])
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.key('ok')
    }

    @Localized
    @BotRequest(path = GET_INLINE)
    getInline() {
        sendMessage
                .key('Main menu')
                .keyboard(inlineKeyboard.button('yes, I confirm', SAMPLE_CONTROLLER + YES_ACTION)
                        .button("no, don't confirm", SAMPLE_CONTROLLER + NO_ACTION, [param: 'some_param', other: 'other_param'])
                        .row()
                        .button('Back', SAMPLE_CONTROLLER + BACK_ACTION)
                )
    }

    @BotRequest(path = YES_ACTION)
    yesAction() {
        sendMessage.key('yes')
    }

    @BotRequest(path = NO_ACTION)
    noAction(String param, String other) {
        sendMessage.key('no')
        sendMessage.key(param)
        sendMessage.key(other)
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.key('back')
        SAMPLE_CONTROLLER + GET_REPLY
    }
}
