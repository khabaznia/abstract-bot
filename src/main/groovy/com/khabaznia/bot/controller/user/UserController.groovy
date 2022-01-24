package com.khabaznia.bot.controller.user

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.security.Role
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_START

@Slf4j
@Component
@BotController
class UserController extends AbstractBotController {

    @Secured(roles = Role.USER)
    @BotRequest(path = USER_START)
    onStart() {
        log.debug "in user start"
    }
}
