package com.khabaznia.bot.security.authorization.voter

import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component


@Slf4j
@Component
class RepeatVoter extends AbstractBotAuthorizationVoter {

    @Autowired
    ApplicationContext context
    @Autowired
    Environment environment

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
//        def currentPath = SessionUtil.getAttribute('updateMessage')
//        log.debug 'User last action -> {}, current action {}', userLastActionFullPath, currentPath
//        def result = userLastActionFullPath == currentPath
//                ? ACCESS_DENIED
//                : ACCESS_GRANTED
//        log.info 'Access {}', result > 0 ? 'granted' : 'denied'
        ACCESS_GRANTED
    }

    private static String getUserLastActionFullPath() {
        SessionUtil.currentChat.lastActionFullPath
    }
}
