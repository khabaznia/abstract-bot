package com.khabaznia.bots.core.routing.interceptor


import com.khabaznia.bots.core.security.authentication.filter.MultiReadHttpServletRequest
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.khabaznia.bots.core.security.authentication.filter.AbstractBotFilter.getUpdate
import static com.khabaznia.bots.core.service.UpdateService.getMessage

@Slf4j
@Component
class UpdateUserDataInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug 'Try to update user data'
        try {
            def wrappedRequest = new MultiReadHttpServletRequest((HttpServletRequest) request)
            def update = getUpdate(wrappedRequest)
            def message = getMessage(update)
            message?.from?.id?.toString() ? userService.updateUser(message.from) : null
        } catch (Exception ex) {
            log.error('[Minor] Cant update user info: ' + ex.message)
        }
        true
    }
}
