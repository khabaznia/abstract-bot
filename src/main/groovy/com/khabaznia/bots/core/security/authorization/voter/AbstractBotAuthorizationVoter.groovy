package com.khabaznia.bots.core.security.authorization.voter

import com.khabaznia.bots.core.routing.proxy.BotControllerProxy
import com.khabaznia.bots.core.routing.proxy.ControllerMetaData
import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.aopalliance.intercept.MethodInvocation
import org.springframework.aop.framework.ReflectiveMethodInvocation
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.vote.AbstractAclVoter
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Slf4j
@Component
abstract class AbstractBotAuthorizationVoter extends AbstractAclVoter implements Loggable {

    protected abstract int voteInternal(Authentication authentication, MethodInvocation method)

    @Override
    int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
        def result = ACCESS_DENIED
        if (object instanceof ReflectiveMethodInvocation && object.target?.class == BotControllerProxy.class) {
            result = voteInternal(authentication, object)
        }
        if (result == ACCESS_DENIED) {
            if (sendWarning()) {
                sendWarnLog("Access denied: " + getMessage())
            }
            log.info "Access denied - {}", getMessage()
        }
        result
    }

    protected abstract boolean sendWarning()

    protected abstract String getMessage()

    protected static ControllerMetaData getMetaData(MethodInvocation method) {
        method?.target?.metaData
    }

    @Override
    boolean supports(ConfigAttribute attribute) {
        true
    }

    @Override
    boolean supports(Class clazz) {
        true
    }
}
