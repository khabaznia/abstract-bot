package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.Emoji.CHECKED_MARK
import static com.khabaznia.bots.core.meta.Emoji.CROSS_MARK

@Slf4j
@Component('booleanFieldProcessingStrategy')
class BooleanFieldProcessingStrategy extends FieldProcessingStrategy {

    private static final Map<Boolean, String> BOOLEAN_VALUES_MAPPING = [(Boolean.TRUE): CHECKED_MARK, (Boolean.FALSE): CROSS_MARK]

    @Override
    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentValueMessage(allIncludedValuesAsString(editFlow))
        messages.editBooleanFieldMenu(editFlow.enterText, editFlow.enterTextBinding)
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        entity?."${editFlow.fieldName}" = Boolean.valueOf(value)
    }

    @Override
    String allIncludedValuesAsString(EditFlow editFlow) {
        BOOLEAN_VALUES_MAPPING.get(covertToType(getPersistedValue(editFlow)))
    }

    @Override
    Boolean covertToType(Object value, Class specificClass) {
        value ? Boolean.valueOf(value.toString()) : Boolean.FALSE
    }
}
