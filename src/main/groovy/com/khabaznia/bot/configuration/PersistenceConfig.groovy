package com.khabaznia.bot.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource


@Configuration
@ImportResource('classpath*:context.xml')
class PersistenceConfig {
}
