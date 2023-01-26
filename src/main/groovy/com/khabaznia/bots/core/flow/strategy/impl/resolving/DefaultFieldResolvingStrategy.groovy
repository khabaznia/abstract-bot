package com.khabaznia.bots.core.flow.strategy.impl.resolving

import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass

@Component('defaultFieldResolvingStrategy')
class DefaultFieldResolvingStrategy extends FieldResolvingStrategy {

    Object getValue(Class entityClass, Long entityId, String fieldName) {
        def fieldClass = getFieldClass(entityClass, fieldName)
        def value = getPersistedValue(entityClass, entityId, fieldName)
        value ? fieldClass.valueOf(value) : null
    }

    @Override
    String getStringView(Object entity, String fieldName) {
        def value = entity."$fieldName"
        value ? value.toString() : null
    }
}
