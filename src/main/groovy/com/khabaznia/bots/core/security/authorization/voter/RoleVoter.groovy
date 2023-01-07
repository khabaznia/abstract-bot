package com.khabaznia.bots.core.security.authorization.voter

import com.khabaznia.bots.core.enums.Role
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.security.Constants.ROLE_PREFIX

@Slf4j
@Component
class RoleVoter extends AbstractBotAuthorizationVoter {

    @Override
    int voteInternal(Authentication authentication, MethodInvocation method) {
        def targetRoles = getMetaData(method)?.roles
        def userRoles = getUserRoles(authentication)
        log.debug 'User roles -> {}, target roles: {},', userRoles, targetRoles

        targetRoles.contains(Role.ALL.toString()) || targetRoles.intersect(userRoles)
                ? ACCESS_GRANTED
                : ACCESS_DENIED
    }

    @Override
    protected String getMessage() {
        'User role not acceptable for this controller.'
    }

    @Override
    protected boolean sendWarning() {
        true
    }

    private static List<String> getUserRoles(Authentication authentication) {
        authentication.authorities.collect { it.role - ROLE_PREFIX }
    }
}
