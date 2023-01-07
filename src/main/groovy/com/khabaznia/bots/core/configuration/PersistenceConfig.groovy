package com.khabaznia.bots.core.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource


@Configuration
@ImportResource('classpath*:context.xml')
class PersistenceConfig {
}
