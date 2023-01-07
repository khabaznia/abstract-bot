package com.khabaznia.bots.core.security.authentication

import com.khabaznia.bots.core.security.ResponseOk
import groovy.util.logging.Slf4j
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@Component
class BotAuthenticationEntryPoint extends BasicAuthenticationEntryPoint implements ResponseOk {

    @Override
    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) {
        log.info 'User is not authenticated'
        log.debug authEx.getMessage()
        setOk response
    }

    @Override
    void afterPropertiesSet() throws Exception {
        setRealmName "OK"
        super.afterPropertiesSet()
    }
}
