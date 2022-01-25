package com.khabaznia.bot.enums

import static com.khabaznia.bot.core.Constants.ADMIN_CHAT_ID
import static com.khabaznia.bot.core.Constants.LOGGING_CHAT_ID

enum LoggingChat {

    DEFAULT(LOGGING_CHAT_ID), ADMIN(ADMIN_CHAT_ID)

    String chatIdConfig

    LoggingChat(String chatIdConfig) {
        this.chatIdConfig = chatIdConfig
    }
}