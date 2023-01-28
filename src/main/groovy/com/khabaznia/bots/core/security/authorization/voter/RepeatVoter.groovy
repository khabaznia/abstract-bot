package com.khabaznia.bots.core.security.authorization.voter

import com.khabaznia.bots.core.util.BotSession
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bots.core.controller.Constants.SESSION_ATTRIBUTES.IS_UPDATE_PROCESSED
import static com.khabaznia.bots.core.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE
import static com.khabaznia.bots.core.routing.Constants.BLOCK_DUPLICATE_REQUESTS
import static com.khabaznia.bots.core.service.UpdateService.getParametersFromMessage

@Slf4j
@Component
class RepeatVoter extends AbstractBotAuthorizationVoter {

    @Autowired
    private ApplicationContext context
    @Autowired
    private Environment env

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def result = ACCESS_GRANTED
        def updateMessage = BotSession.getStringAttribute(UPDATE_MESSAGE)
        if (isFeatureEnabled()
                && isNotRedirectCallFromController()
                && isNotSpecialButton(updateMessage)
                && isControllerApplicable(method)) {
            log.debug 'User last action -> {}, current action {}', userLastActionFullPath, updateMessage
            result = userLastActionFullPath == updateMessage
                    ? ACCESS_DENIED
                    : ACCESS_GRANTED
        }
        BotSession.setAttribute(IS_UPDATE_PROCESSED, '')
        result
    }

    @Override
    protected boolean sendWarning() {
        false
    }

    @Override
    protected String getMessage() {
        'User again tries access to resource.'
    }

    private static boolean isControllerApplicable(MethodInvocation method) {
        !getMetaData(method)?.enableDuplicateRequests
    }

    private static boolean isNotRedirectCallFromController() {
        BotSession.getStringAttribute(IS_UPDATE_PROCESSED) && BotSession.getStringAttribute(IS_UPDATE_PROCESSED) != ''
    }

    private boolean isFeatureEnabled() {
        Boolean.valueOf(env.getProperty(BLOCK_DUPLICATE_REQUESTS))
    }

    private static boolean isNotSpecialButton(String updateMessage) {
        !Boolean.valueOf(getParametersFromMessage(updateMessage)?.get(UNLIMITED_CALL))
    }

    private static String getUserLastActionFullPath() {
        BotSession.currentChat.lastActionFullPath
    }
}
