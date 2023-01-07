package com.khabaznia.bots.core.security.authorization

import com.khabaznia.bots.core.security.authorization.voter.FlowVoter
import com.khabaznia.bots.core.security.authorization.voter.RepeatVoter
import com.khabaznia.bots.core.security.authorization.voter.RestrictedModeVoter
import com.khabaznia.bots.core.security.authorization.voter.RoleVoter
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.vote.UnanimousBased
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

import javax.annotation.Resource

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class MethodSecurityVoterConfiguration extends GlobalMethodSecurityConfiguration {

    @Resource
    private RoleVoter roleVoter
    @Resource
    private FlowVoter flowVoter
    @Resource
    private RepeatVoter repeatVoter
    @Resource
    private RestrictedModeVoter restrictedModeVoter

    @Override
    AccessDecisionManager accessDecisionManager() {
        new UnanimousBased([roleVoter, flowVoter, repeatVoter, restrictedModeVoter])
    }
}
