package com.khabaznia.bot.security.authentication.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.khabaznia.bot.service.UpdateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import org.telegram.telegrambots.meta.api.objects.Update

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY

@Slf4j
@Component
class BotUserSecurityFilter extends GenericFilterBean {

    @Autowired
    private AuthenticationManager authManager

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug 'Try to get auth user'
        def wrappedRequest = new MultiReadHttpServletRequest((HttpServletRequest) request)
        if (hasUpdate(wrappedRequest))
            authenticateUser(getUserCode(wrappedRequest), wrappedRequest)
        log.trace 'Authentication successful'
        chain.doFilter(wrappedRequest, response)
    }

    private void authenticateUser(String userCode, MultiReadHttpServletRequest wrappedRequest) {
        try {
            def auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(userCode, userCode))
            def securityContext = SecurityContextHolder.context
            securityContext.setAuthentication(auth)
            def session = ((HttpServletRequest) wrappedRequest).getSession(true)
            session.setAttribute SPRING_SECURITY_CONTEXT_KEY, securityContext
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Authentication failed ".concat(ex.message), ex)
        }
    }

    private static String getUserCode(MultiReadHttpServletRequest wrappedRequest) throws MismatchedInputException {
        def req = wrappedRequest.getReader().readLines().join()
        def update = wrappedRequest.getAttribute('update') ?: new ObjectMapper().readValue(req, Update.class)
        UpdateService.getChatInfoFromUpdate(update as Update)
    }

    private static Boolean hasUpdate(MultiReadHttpServletRequest wrappedRequest) {
        return wrappedRequest.getAttribute('update') || wrappedRequest.getReader().readLines().join()
    }
}
