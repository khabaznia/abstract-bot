package com.khabaznia.bots.core.security

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@Order(1)
class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/")
                .permitAll()
    }
}
