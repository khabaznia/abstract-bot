package com.khabaznia.bots.core.flow.strategy.impl.processing

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.flow.validation.InputNumberValidator
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getClass
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass

@Slf4j
@Component('numberFieldProcessingStrategy')
class NumberFieldProcessingStrategy extends FieldProcessingStrategy {

    @Override
    void validate(EditFlow editFlow, String value) {
        if (value)
            InputNumberValidator.validate(value, getFieldClass(getClass(editFlow), editFlow.fieldName))
        super.validate(editFlow, value)
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        def fieldClass = getFieldClass(getClass(editFlow), editFlow.fieldName)
        entity?."${editFlow.fieldName}" = covertToType(value, fieldClass)
    }
}
