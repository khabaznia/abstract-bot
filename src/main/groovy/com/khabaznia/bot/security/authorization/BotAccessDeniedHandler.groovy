package com.khabaznia.bot.security.authorization

import com.khabaznia.bot.security.ResponseOk
import groovy.util.logging.Slf4j
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@Component
class BotAccessDeniedHandler implements AccessDeniedHandler, ResponseOk {

    @Override
    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authEx) {
        log.info 'User is not authorized'
        log.debug authEx.getMessage()
        setOk(response)
    }
}
