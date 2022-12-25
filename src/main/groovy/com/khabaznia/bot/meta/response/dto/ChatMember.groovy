package com.khabaznia.bot.meta.response.dto

import com.khabaznia.bot.enums.ChatMemberStatus
import groovy.transform.ToString

@ToString
class ChatMember {

    User user
    ChatMemberStatus status
}
