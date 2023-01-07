package com.khabaznia.bots.core.security.authentication.filter

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.telegram.telegrambots.meta.api.objects.Update

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

@Slf4j
abstract class AbstractBotFilter implements Filter {

    @Value('${env.only.bot.token}')
    private String botToken

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        def wrappedRequest = new MultiReadHttpServletRequest((HttpServletRequest) request)
        if (shouldFilter(wrappedRequest)) {
            doFilterInternal(wrappedRequest)
        }
        chain.doFilter(wrappedRequest, response)
    }

    abstract void doFilterInternal(MultiReadHttpServletRequest wrappedRequest)

    private boolean shouldFilter(MultiReadHttpServletRequest request) {
        "/$botToken" == request.getRequestURI()
    }

    static Update getUpdate(MultiReadHttpServletRequest wrappedRequest) {
        def req = wrappedRequest.getReader().readLines().join()
        wrappedRequest.getAttribute('update') ?: new ObjectMapper().readValue(req, Update.class)
    }

    static Boolean hasUpdate(MultiReadHttpServletRequest wrappedRequest) {
        return wrappedRequest.getAttribute('update') || wrappedRequest.getReader().readLines().join()
    }
}

