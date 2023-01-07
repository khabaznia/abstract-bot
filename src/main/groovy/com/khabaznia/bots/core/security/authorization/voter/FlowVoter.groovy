package com.khabaznia.bots.core.security.authorization.voter

import com.khabaznia.bots.core.util.SessionUtil
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.routing.Constants.AVAILABLE_LOCALES
import static com.khabaznia.bots.core.routing.Constants.CONFIGS_DELIMITER


@Slf4j
@Component
class FlowVoter extends AbstractBotAuthorizationVoter {

    @Autowired
    private ApplicationContext context
    @Autowired
    private Environment environment

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def controllerPreviousPath = getMetaData(method)?.previousPath
        log.debug 'User last action -> {}, target previous path {}', userLastAction, controllerPreviousPath
        checkUserLastAction(controllerPreviousPath, userLastAction as String)
                ? ACCESS_GRANTED
                : ACCESS_DENIED
    }

    @Override
    protected String getMessage() {
        'Flow is broken. User\'s previous path mismatch to previous path of controller'
    }

    @Override
    protected boolean sendWarning() {
        false
    }

    private boolean checkUserLastAction(String previousPath, String lastAction) {
        previousPath.isBlank()
                || lastAction?.endsWith(previousPath)
                || getLocalizedPaths(previousPath).any { lastAction?.endsWith(it) }
    }

    private List<String> getLocalizedPaths(String previousPath) {
        environment.getProperty(AVAILABLE_LOCALES).tokenize(CONFIGS_DELIMITER)
                .collect { context.getMessage(previousPath, null, new Locale(it)) }.unique()
    }

    private static String getUserLastAction() {
        SessionUtil.currentChat.lastAction
    }
}
