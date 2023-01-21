package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass

@Component('defaultFieldResolvingStrategyMap')
class DefaultFieldResolvingStrategy extends FieldResolvingStrategy {

    Object getValue(Class entityClass, Long entityId, String fieldName) {
        def fieldClass = getFieldClass(entityClass, fieldName)
        fieldClass.valueOf(getPersistedValue(entityClass, entityId, fieldName))
    }
}
