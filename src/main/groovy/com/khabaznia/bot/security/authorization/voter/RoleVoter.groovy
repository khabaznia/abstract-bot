package com.khabaznia.bot.security.authorization.voter

import com.khabaznia.bot.security.Role
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bot.security.Constants.*

@Slf4j
@Component
class RoleVoter extends AbstractBotAuthorizationVoter {

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def targetRoles = getMetaData(method)?.roles
        def userRole = getUserRoles(authentication)
        log.debug 'User role -> {}, target roles: {},', userRole, targetRoles

        def result = targetRoles.contains(Role.ALL.toString()) || targetRoles.intersect(userRole)
                ? ACCESS_GRANTED
                : ACCESS_DENIED
        log.info 'Access {}', result > 0 ? 'granted' : 'denied'
        result
    }

    @Override
    protected String getMessage() {
        'User role not acceptable for this controller.'
    }

    private static List<String> getUserRoles(Authentication authentication) {
        authentication.authorities.collect { it.role - ROLE_PREFIX }
    }
}
