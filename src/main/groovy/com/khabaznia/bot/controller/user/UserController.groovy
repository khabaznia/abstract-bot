package com.khabaznia.bot.controller.user

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.enums.Role
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.EXAMPLE
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.*
import static com.khabaznia.bot.meta.Emoji.FINGER_DOWN
import static com.khabaznia.bot.meta.Emoji.GEAR
import static com.khabaznia.bot.meta.Emoji.TEST_EMOJI_SET
import static com.khabaznia.bot.meta.Emoji.SCREAMING_FACE

@Slf4j
@Component
@BotController
class UserController extends AbstractBotController {

    @Secured(roles = Role.USER)
    @BotRequest(path = USER_START)
    String onStart() {
        sendMessage.text('message.user.greeting')
                .emoji(SCREAMING_FACE)
        log.debug 'User on start'
        USER_TO_MAIN
    }

    @Secured(roles = Role.USER)
    @BotRequest(path = USER_TO_MAIN)
    userMenu() {
        sendMessage.text('message.choose.action')
                .emoji(FINGER_DOWN)
                .replyKeyboard([[EXAMPLE.addEmoji(TEST_EMOJI_SET)], [SETTINGS.addEmoji(GEAR)]])
        log.debug 'User menu'
    }
}
