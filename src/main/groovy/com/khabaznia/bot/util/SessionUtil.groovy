package com.khabaznia.bot.util

import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User
import groovy.util.logging.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder

import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION

@Slf4j
class SessionUtil {

    static Chat getCurrentChat() {
        SecurityContextHolder.context?.authentication?.principal?.fromChat as Chat
    }

    static User getCurrentUser() {
        SecurityContextHolder.context?.authentication?.principal?.fromUser as User
    }

    static void setAttribute(String name, String value) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, value, SCOPE_SESSION)
    }

    static String getAttribute(String name) {
        RequestContextHolder?.getRequestAttributes()?.getAttribute(name, SCOPE_SESSION)
    }

}
