package com.khabaznia.bots.core.flow.strategy

import com.khabaznia.bots.core.flow.validation.InputNumberValidator
import com.khabaznia.bots.core.model.EditFlow
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getClass

@Slf4j
@Component('numberFieldProcessingStrategy')
class NumberFieldProcessingStrategy extends FieldProcessingStrategy {

    @Override
    void validate(EditFlow editFlow, String value) {
        InputNumberValidator.validate(value, getFieldClass(getClass(editFlow), editFlow.fieldName))
        super.validate(editFlow, value)
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        def fieldClass = getFieldClass(getClass(editFlow), editFlow.fieldName)
        entity?."${editFlow.fieldName}" = fieldClass.valueOf(value)
    }

    @Override
    Number covertToType(Object value, Class specificClass) {
        specificClass.valueOf(value)
    }
}
