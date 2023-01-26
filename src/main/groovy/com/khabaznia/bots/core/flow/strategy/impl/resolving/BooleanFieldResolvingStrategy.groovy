package com.khabaznia.bots.core.flow.strategy.impl.resolving

import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.strategy.impl.processing.BooleanFieldProcessingStrategy.BOOLEAN_VALUES_MAPPING
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass

@Component('booleanFieldResolvingStrategy')
class BooleanFieldResolvingStrategy extends FieldResolvingStrategy {

    Object getValue(Class entityClass, Long entityId, String fieldName) {
        def fieldClass = getFieldClass(entityClass, fieldName)
        def persistedValue = getPersistedValue(entityClass, entityId, fieldName)
        persistedValue ? fieldClass.valueOf(persistedValue) : null
    }

    @Override
    String getStringView(Object entity, String fieldName) {
        BOOLEAN_VALUES_MAPPING.get(Boolean.TRUE == entity."$fieldName")
    }
}
