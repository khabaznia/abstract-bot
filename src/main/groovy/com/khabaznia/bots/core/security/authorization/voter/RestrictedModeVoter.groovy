package com.khabaznia.bots.core.security.authorization.voter

import com.khabaznia.bots.core.util.BotSession
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.routing.Constants.*

@Slf4j
@Component
class RestrictedModeVoter extends AbstractBotAuthorizationVoter {

    @Autowired
    private Environment env

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        if (isFeatureEnabled()) {
            def allowedUsers = env.getProperty(RESTRICTED_MODE_USERS)?.tokenize(CONFIGS_DELIMITER)
            def chatCode = [BotSession.getCurrentChat().code, BotSession.currentUser.code]
            log.trace 'Allowed users -> {}. User & chat codes -> {}', allowedUsers, chatCode
            return allowedUsers.intersect(chatCode)
                    ? ACCESS_GRANTED
                    : ACCESS_DENIED
        }
        ACCESS_GRANTED
    }

    @Override
    protected boolean sendWarning() {
        true
    }

    @Override
    protected String getMessage() {
        'User is not in list of restricted users.'
    }

    private boolean isFeatureEnabled() {
        Boolean.valueOf(env.getProperty(RESTRICTED_MODE))
    }
}
