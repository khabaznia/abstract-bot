package com.khabaznia.bot.util

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.enums.UserLogType
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User

import static com.khabaznia.bot.util.SessionUtil.*

class LoggingUtil {

    static String getUserInfo(String userCode){
        userCode.linkUrl(userCode.userMentionUrl())
    }

    static String getUsersInfo(List<String> userCodes){
        userCodes.collect { getUserInfo(it) }.join(' ')
    }

    static String getUserInfo(User user = currentUser, UserLogType logType = UserLogType.ONLY_MENTION_CODE) {
        !user ? '' :
                (user.role == UserRole.BOT || user.role == user.role.ADMIN)
                        ? user.role.toString().bold()
                        : getUserMentionInfo(user, logType)
    }

    private static String getUserMentionInfo(User user, UserLogType logType) {
        switch (logType) {
            case UserLogType.ONLY_NAME_OR_USERNAME:
                return "$user.firstName" + (user.lastName ? " $user.lastName" : '')
            case UserLogType.ONLY_MENTION_CODE:
                return user?.code?.linkUrl(mentionUrl(user))
            case UserLogType.ONLY_MENTION_NAME_OR_USERNAME:
                return getUserFirstLastName(user)
                        ? getUserFirstLastName(user).linkUrl(mentionUrl(user))
                        : getUserName(user)
            case UserLogType.FULL:
                return user?.code?.linkUrl(mentionUrl(user)) + ' ' + getUserFirstLastName(user) + ' ' + getUserName(user)
        }
    }

    private static String mentionUrl(User user) {
        user?.code?.userMentionUrl()
    }


    private static String getUserFirstLastName(User user) {
        "$user.firstName" + (user.lastName ? " $user.lastName" : '')
    }

    private static String getUserName(User user) {
        user.username ? "@$user.username" : ''
    }

    static String getChatInfo(Chat chat = currentChat) {
        !chat ? '' : "Chat ${chat.code?.bold()} " +
                "${chat.role != ChatRole.NONE ? chat.role.toString() : ''}"
    }

}
