package com.khabaznia.bots.core.flow.strategy

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component('stringFieldProcessingStrategy')
class StringFieldProcessingStrategy extends FieldProcessingStrategy {

    @Override
    String covertToType(Object value, Class specificClass) {
        value.toString()
    }
}
