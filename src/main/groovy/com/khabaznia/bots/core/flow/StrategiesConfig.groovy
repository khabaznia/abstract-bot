package com.khabaznia.bots.core.flow

import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Slf4j
@Configuration
class StrategiesConfig {

    @Autowired
    private ApplicationContext context

    @Bean(name = 'fieldProcessingStrategiesMap')
    Map<FieldType, FieldProcessingStrategy> requestProcessingStrategyMap() {
        Map<FieldType, FieldProcessingStrategy> map = [:]
        map.put(FieldType.STRING, context.getBean('stringFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.LOCALIZED, context.getBean('localizedFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.NUMBER, context.getBean('numberFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.BOOLEAN, context.getBean('booleanFieldProcessingStrategy') as FieldProcessingStrategy)
        map
    }
}
