package com.khabaznia.bot.security.authorization.voter

import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bot.configuration.CustomLocaleResolver.AVAILABLE_LOCALES
import static com.khabaznia.bot.core.Constants.CONFIGS_DELIMITER


@Slf4j
@Component
class FlowVoter extends AbstractBotAuthorizationVoter {

    @Autowired
    ApplicationContext context
    @Autowired
    Environment environment

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def controllerPreviousPath = getMetaData(method)?.previousPath
        log.debug 'User last action -> {}, target previous path {}', userLastAction, controllerPreviousPath
        def result = checkUserLastAction(controllerPreviousPath, userLastAction as String)
                ? ACCESS_GRANTED
                : ACCESS_DENIED
        log.info 'Access {}', result > 0 ? 'granted' : 'denied'
        result
    }

    private boolean checkUserLastAction(String previousPath, String lastAction) {
        previousPath.isBlank()
                || lastAction?.endsWith(previousPath)
                || getLocalizedPaths(previousPath).any { lastAction?.endsWith(it) }
    }

    private List<String> getLocalizedPaths(String previousPath) {
        environment.getProperty(AVAILABLE_LOCALES).split(CONFIGS_DELIMITER)
                .collect { context.getMessage(previousPath, null, new Locale(it)) }.unique()

    }

    private static String getUserLastAction() {
        SessionUtil.currentChat.lastAction
    }
}