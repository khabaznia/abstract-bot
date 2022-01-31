package com.khabaznia.bot.security.authorization.voter

import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import io.micrometer.core.instrument.util.StringUtils
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE_ATTR
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID_ATTR
import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bot.core.Constants.BLOCK_DUPLICATE_REQUESTS

@Slf4j
@Component
class RepeatVoter extends AbstractBotAuthorizationVoter implements Configurable {

    @Autowired
    private ApplicationContext context
    @Autowired
    private Environment environment
    @Autowired
    private UpdateService updateService

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def result = ACCESS_GRANTED
        def updateMessage = SessionUtil.getAttribute(UPDATE_MESSAGE_ATTR)
        if (isFeatureEnabled() && isNotRedirectCallFromController() && isNotSpecialButton(updateMessage)) {
            log.debug 'User last action -> {}, current action {}', userLastActionFullPath, updateMessage
            result = userLastActionFullPath == updateMessage
                    ? ACCESS_DENIED
                    : ACCESS_GRANTED
            log.info 'Access {}', result > 0 ? 'granted' : 'denied'
        }
        SessionUtil.setAttribute(UPDATE_ID_ATTR, '')
        result
    }

    private boolean isNotRedirectCallFromController() {
        StringUtils.isNotEmpty(SessionUtil.getAttribute(UPDATE_ID_ATTR))
    }

    private boolean isFeatureEnabled() {
        isEnabled(BLOCK_DUPLICATE_REQUESTS)
    }

    private boolean isNotSpecialButton(String updateMessage) {
        !Boolean.valueOf(updateService.getParametersFromMessage(updateMessage)?.get(UNLIMITED_CALL))
    }

    private static String getUserLastActionFullPath() {
        SessionUtil.currentChat.lastActionFullPath
    }
}
