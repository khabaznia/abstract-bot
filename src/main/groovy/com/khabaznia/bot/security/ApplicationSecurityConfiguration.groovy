package com.khabaznia.bot.security

import com.khabaznia.bot.security.authentication.BotAuthenticationEntryPoint
import com.khabaznia.bot.security.authentication.BotUserDetailsService
import com.khabaznia.bot.security.authentication.filter.BotUserSecurityFilter
import com.khabaznia.bot.security.authorization.BotAccessDeniedHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import javax.annotation.Resource

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    BotUserDetailsService userDetailsService
    @Autowired
    BotAuthenticationEntryPoint botAuthenticationEntryPoint
    @Autowired
    BotAccessDeniedHandler botAccessDeniedHandler
    @Resource
    BotUserSecurityFilter filter

    @Bean("authenticationManager")
    @Override
    AuthenticationManager authenticationManagerBean() throws Exception {
        super.authenticationManagerBean()
    }

    @Bean
    MethodInvokingFactoryBean methodInvokingFactoryBean() {
        new MethodInvokingFactoryBean(targetClass: SecurityContextHolder.class,
                targetMethod: 'setStrategyName',
                arguments: [SecurityContextHolder.MODE_INHERITABLETHREADLOCAL])
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        def provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService userDetailsService
        provider.setPasswordEncoder NoOpPasswordEncoder.getInstance()
        provider.setAuthoritiesMapper authoritiesMapper()
        provider
    }

    @Bean
    GrantedAuthoritiesMapper authoritiesMapper() {
        new SimpleAuthorityMapper(convertToUpperCase: true)
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider())
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterAfter(filter, BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(botAccessDeniedHandler)
                .authenticationEntryPoint(botAuthenticationEntryPoint)
    }
}
