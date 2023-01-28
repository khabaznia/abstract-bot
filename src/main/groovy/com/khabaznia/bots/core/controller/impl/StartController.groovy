package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.meta.Emoji
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.ADMIN_CONTROLLER.ADMIN_START
import static com.khabaznia.bots.common.Constants.ADMIN_CONTROLLER.ADMIN_TO_MAIN
import static com.khabaznia.bots.common.Constants.USER_CONTROLLER.USER_START
import static com.khabaznia.bots.common.Constants.USER_CONTROLLER.USER_TO_MAIN
import static com.khabaznia.bots.core.controller.Constants.COMMON.*
import static com.khabaznia.bots.core.util.BotSession.getCurrentChat
import static com.khabaznia.bots.core.util.BotSession.getCurrentUser

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
