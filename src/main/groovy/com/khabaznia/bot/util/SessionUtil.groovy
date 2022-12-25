package com.khabaznia.bot.util

import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User
import com.khabaznia.bot.security.authentication.BotUserPrincipal
import groovy.util.logging.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder

import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION

@Slf4j
class SessionUtil {

    static Chat getCurrentChat() {
        principal?.fromChat
    }

    static User getCurrentUser() {
        principal?.fromUser
    }

    private static BotUserPrincipal getPrincipal() {
        def principal = SecurityContextHolder.context?.authentication?.principal
        (principal && principal instanceof BotUserPrincipal) ? principal : null
    }

    static void setAttribute(String name, Object value) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, value, SCOPE_SESSION)
    }

    static String getStringAttribute(String name) {
        RequestContextHolder?.getRequestAttributes()?.getAttribute(name, SCOPE_SESSION)?.toString()
    }

}
