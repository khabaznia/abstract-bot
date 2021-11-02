package com.khabaznia.bot.controller.sample

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SAMPLE_CONTROLLER.*

@Slf4j
@Component
@BotController(path = SAMPLE_CONTROLLER)
class SampleController extends AbstractBotController {

    @BotRequest(path = GET_REPLY)
    getReply() {
        sendMessage
                .key('hi')
                .keyboard([ACTION_ONE, ACTION_TWO, GET_INLINE])
    }

    @Localized
    @BotRequest(path = ACTION_ONE)
    String getFeatures() {
        sendMessage.key('action one')
    }

    @Localized
    @BotRequest(path = ACTION_TWO)
    String actionTwo() {
        sendMessage.key('action two')
        GET_REPLY
    }

    @Localized
    @BotRequest(path = GET_INLINE)
    getInline() {
        sendMessage
                .key('hi')
                .keyboard(inlineKeyboard.addButton('yes, I confirm', YES_ACTION)
                        .addButton("no, don't confirm", NO_ACTION, [param: 'some_param'])
                        .addRow()
                        .addButton('Back', BACK_ACTION)
                )
    }

    @BotRequest(path = YES_ACTION)
    yesAction() {
        sendMessage.key('yes')
    }

    @BotRequest(path = NO_ACTION)
    noAction(String param) {
        sendMessage.key('no')
        sendMessage.key(param)
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.key('back')
        GET_REPLY
    }
}
