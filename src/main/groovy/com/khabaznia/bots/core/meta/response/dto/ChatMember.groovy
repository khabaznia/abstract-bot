package com.khabaznia.bots.core.meta.response.dto

import com.khabaznia.bots.core.enums.ChatMemberStatus
import groovy.transform.ToString

@ToString
class ChatMember {

    User user
    ChatMemberStatus status
}
