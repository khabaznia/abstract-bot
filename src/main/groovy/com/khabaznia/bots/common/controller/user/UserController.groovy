package com.khabaznia.bots.common.controller.user

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Secured
import com.khabaznia.bots.core.enums.Role
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.util.DefaultRoleKeyboardsUtil.getUserReplyKeyboard
import static com.khabaznia.bots.common.Constants.USER_CONTROLLER.USER_START
import static com.khabaznia.bots.common.Constants.USER_CONTROLLER.USER_TO_MAIN
import static com.khabaznia.bots.core.meta.Emoji.*

@Slf4j
@Component
@BotController
class UserController extends AbstractBotController {

    @Secured(roles = Role.USER)
    @BotRequest(path = USER_START)
    String onStart() {
        sendMessage.text('text.user.greeting')
                .emoji(SCREAMING_FACE)
        log.debug 'User on start'
        USER_TO_MAIN
    }

    @Secured(roles = Role.USER)
    @BotRequest(path = USER_TO_MAIN)
    userMenu() {
        sendMessage.text('text.choose.action')
                .emoji(FINGER_DOWN)
                .replyKeyboard(getUserReplyKeyboard())
        log.debug 'User menu'
    }
}
