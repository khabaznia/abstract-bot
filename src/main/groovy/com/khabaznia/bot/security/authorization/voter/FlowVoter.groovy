package com.khabaznia.bot.security.authorization.voter

import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component


@Slf4j
@Component
class FlowVoter extends AbstractBotAuthorizationVoter {

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def controllerPreviousPath = getMetaData(method)?.previousPath
        log.debug 'User last action -> {}, target previous path {}', userLastAction, controllerPreviousPath

        def result = controllerPreviousPath.isBlank() || userLastAction?.endsWith(controllerPreviousPath)
                ? ACCESS_GRANTED
                : ACCESS_DENIED
        log.info 'Access {}', result > 0 ? 'granted' : 'denied'
        result
    }

    static String getUserLastAction() {
        SessionUtil.currentChat.lastAction
    }
}
