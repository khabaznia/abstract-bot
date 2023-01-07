package com.khabaznia.bots.common.controller.common

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.routing.annotation.Action
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.COMMON.DEFAULT

@Slf4j
@Component
@BotController
class DefaultController extends AbstractBotController {

    @Action(skip = true)
    @BotRequest(path = DEFAULT, enableDuplicateRequests = true)
    defaultAction() {
        log.debug 'In default controller'
    }

}
