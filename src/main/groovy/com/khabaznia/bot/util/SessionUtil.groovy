package com.khabaznia.bot.util

import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User
import org.springframework.security.core.context.SecurityContextHolder

class SessionUtil {

    static Chat getCurrentChat(){
        SecurityContextHolder.context.authentication.principal?.fromChat as Chat
    }

    static User getCurrentUser(){
        SecurityContextHolder.context.authentication.principal?.fromUser as User
    }
}
