package com.khabaznia.bots.common.util

import com.khabaznia.bots.common.Constants
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Configurable
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.ADMIN_CONTROLLER.FEATURES_LIST
import static com.khabaznia.bots.common.Constants.COMMON.*
import static com.khabaznia.bots.common.Constants.LANG_CONTROLLER.DISPLAY_CHANGE_LANG
import static com.khabaznia.bots.example.Constants.EXAMPLE
import static com.khabaznia.bots.core.routing.Constants.USE_ONLY_DEFAULT_LANGUAGE
import static com.khabaznia.bots.core.meta.Emoji.*
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentUser


@Component
class DefaultRoleKeyboardsUtil implements Configurable, BaseRequests {

    static List<List<String>> getAdminReplyKeyboard() {
        [[EXAMPLE], [SETTINGS.addEmoji(TOOLS)]]
    }

    static List<List<String>> getUserReplyKeyboard() {
        [[EXAMPLE], [SETTINGS.addEmoji(TOOLS)]]
    }

    ReplyKeyboard getSettingsKeyboard() {
        def keyboard = replyKeyboard
        adminButtons(keyboard)
        userButtons(keyboard)
        keyboard.row()
        langButton(keyboard)
        keyboard.button(TO_MAIN, LEFT_ARROW)
        keyboard
    }

    private adminButtons(ReplyKeyboard keyboard) {
        if (currentChat.code != adminChatId) return
        keyboard.button(FEATURES_LIST, GEAR)
    }

    private userButtons(ReplyKeyboard keyboard) {
        if (currentChat.code == adminChatId) return
        keyboard.button(GENERAL_SUBSCRIPTION_NOTIFY_ALL.addEmoji(currentUser.subscription.general
                ? CHECKED_MARK
                : CROSS_MARK))
    }

    private void langButton(ReplyKeyboard keyboard) {
        if (!isEnabled(USE_ONLY_DEFAULT_LANGUAGE))
            keyboard.button(DISPLAY_CHANGE_LANG, Constants.LANG_CONTROLLER.LANG_EMOJI.get(currentChat.lang)).row()
    }
}
