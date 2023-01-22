package com.khabaznia.bots.core.flow

import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
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
    Map<FieldType, FieldProcessingStrategy> fieldProcessingStrategyMap() {
        Map<FieldType, FieldProcessingStrategy> map = [:]
        map.put(FieldType.STRING, context.getBean('stringFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.LOCALIZED, context.getBean('localizedFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.NUMBER, context.getBean('numberFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.BOOLEAN, context.getBean('booleanFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.COLLECTION, context.getBean('collectionFieldProcessingStrategy') as FieldProcessingStrategy)
        map.put(FieldType.MEDIA, context.getBean('mediaFieldProcessingStrategy') as FieldProcessingStrategy)
        map
    }

    @Bean(name = 'fieldResolvingStrategiesMap')
    Map<FieldType, FieldResolvingStrategy> fieldResolvingStrategyMap() {
        Map<FieldType, FieldResolvingStrategy> map = [:]
        map.put(FieldType.STRING, context.getBean('defaultFieldResolvingStrategyMap') as FieldResolvingStrategy)
        map.put(FieldType.NUMBER, context.getBean('defaultFieldResolvingStrategyMap') as FieldResolvingStrategy)
        map.put(FieldType.BOOLEAN, context.getBean('defaultFieldResolvingStrategyMap') as FieldResolvingStrategy)
        map.put(FieldType.LOCALIZED, context.getBean('localizedFieldResolvingStrategyMap') as FieldResolvingStrategy)
        map
    }
}
