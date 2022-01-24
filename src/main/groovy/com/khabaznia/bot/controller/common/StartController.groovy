package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*

@Slf4j
@Component
@BotController
class StartController extends AbstractBotController {

    @BotRequest(path = START)
    String onStart() {
        REPLY_KEYBOARD
    }

    @BotRequest(path = MENU)
    String getMain() {
        REPLY_KEYBOARD
    }

}
