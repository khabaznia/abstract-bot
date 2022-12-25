package com.khabaznia.bot.controller.user

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.enums.Role
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.util.DefaultRoleKeyboardsUtil.getUserReplyKeyboard
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_START
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_TO_MAIN
import static com.khabaznia.bot.meta.Emoji.*

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
