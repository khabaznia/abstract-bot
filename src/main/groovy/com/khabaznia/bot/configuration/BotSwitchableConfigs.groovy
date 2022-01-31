package com.khabaznia.bot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = 'switchable')
class BotSwitchableConfigs {

    Map<String, String> config

    @Bean
    Map<String, String> switchableConfigs() {
        config
    }
}
