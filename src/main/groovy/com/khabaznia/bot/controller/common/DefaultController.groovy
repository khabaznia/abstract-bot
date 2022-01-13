package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT

@Slf4j
@Component
@BotController
class DefaultController extends AbstractBotController {

    @BotRequest(path = DEFAULT)
    defaultAction() {
    }

}
