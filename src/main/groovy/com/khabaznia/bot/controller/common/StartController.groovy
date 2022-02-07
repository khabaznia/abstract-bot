package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.ChatType
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_START
import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_TO_MAIN
import static com.khabaznia.bot.controller.Constants.COMMON.*
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
        SessionUtil.currentChat.type == ChatType.PRIVATE
                ? (SessionUtil.currentUser.role == UserRole.ADMIN ? ADMIN_START : USER_START)
                : TO_MAIN
    }

    @Localized
    @BotRequest(path = TO_MAIN)
    String getMain() {
        if (SessionUtil.currentChat.type == ChatType.PRIVATE)
            return SessionUtil.currentUser.role == UserRole.ADMIN ? ADMIN_TO_MAIN : USER_TO_MAIN

        sendMessage.text('message.choose.action')
                .emoji(FINGER_DOWN)
                .keyboard([SETTINGS.addEmoji(GEAR)])
                .type(MessageType.DELETE)
        DEFAULT
    }

}
