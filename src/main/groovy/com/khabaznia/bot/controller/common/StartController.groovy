package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_TO_MAIN
import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_START
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_START
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_TO_MAIN
import static com.khabaznia.bot.meta.Emoji.FINGER_DOWN
import static com.khabaznia.bot.meta.Emoji.GEAR

@Slf4j
@Component
@BotController
class StartController extends AbstractBotController {

    @BotRequest(path = START)
    String onStart() {
        switch (SessionUtil.currentUser.role) {
            case UserRole.ADMIN:
                return ADMIN_START
            case UserRole.USER:
                return USER_START
        }
        TO_MAIN
    }

    @Localized
    @BotRequest(path = TO_MAIN)
    String getMain() {
        switch (SessionUtil.currentUser.role) {
            case UserRole.ADMIN:
                return ADMIN_TO_MAIN
            case UserRole.USER:
                return USER_TO_MAIN
        }
        sendMessage.key('message.choose.action')
                .emoji(FINGER_DOWN)
                .keyboard([SETTINGS.addEmoji(GEAR)])
                .type(MessageType.DELETE)
        DEFAULT
    }

}
