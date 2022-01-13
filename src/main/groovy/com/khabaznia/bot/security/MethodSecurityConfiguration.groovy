package com.khabaznia.bot.security

import com.khabaznia.bot.security.authorization.voter.FlowVoter
import com.khabaznia.bot.security.authorization.voter.RoleVoter
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.vote.UnanimousBased
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

import javax.annotation.Resource

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Resource
    RoleVoter roleVoter
    @Resource
    FlowVoter flowVoter

    @Override
    AccessDecisionManager accessDecisionManager() {
        new UnanimousBased([roleVoter, flowVoter])
    }
}
