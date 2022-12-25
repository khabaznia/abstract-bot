package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.ChatType
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_START
import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.ADMIN_TO_MAIN
import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_START
import static com.khabaznia.bot.controller.Constants.USER_CONTROLLER.USER_TO_MAIN
import static com.khabaznia.bot.util.SessionUtil.getCurrentChat
import static com.khabaznia.bot.util.SessionUtil.getCurrentUser

@Slf4j
@Component
@BotController
class StartController extends AbstractBotController {

    @BotRequest(path = START, enableDuplicateRequests = true)
    String onStart() {
        currentChat.type == ChatType.PRIVATE
                ? (currentUser.role == UserRole.ADMIN ? ADMIN_START : USER_START)
                : DELETE_REPLY_KEYBOARD
    }

    @Localized
    @BotRequest(path = TO_MAIN)
    String getMain() {
        currentChat.type == ChatType.PRIVATE
                ? currentUser.role == UserRole.ADMIN ? ADMIN_TO_MAIN : USER_TO_MAIN
                : DELETE_REPLY_KEYBOARD
    }

    @BotRequest(path = DELETE_REPLY_KEYBOARD)
    deleteReplyKeyboard() {
        sendMessage.text(Emoji.OKAY).keyboard(replyKeyboardRemove)
    }

}
