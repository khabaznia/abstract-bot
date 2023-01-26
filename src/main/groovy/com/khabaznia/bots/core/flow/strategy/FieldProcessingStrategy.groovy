package com.khabaznia.bots.core.flow.strategy

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.util.EditFlowMessages
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static javax.validation.Validation.buildDefaultValidatorFactory

@Slf4j
abstract class FieldProcessingStrategy {

    @Autowired
    protected EntityManager entityManager
    @Autowired
    protected EditFlowMessages messages

    void prepare(EditFlow editFlow) {
        editFlow.type = getFieldType(getClass(editFlow), editFlow.fieldName)
        editFlow.oldValue = allIncludedValuesAsString(editFlow)
    }

    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentValueMessage(allIncludedValuesAsString(editFlow))
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    void validate(EditFlow editFlow, String value) {
        def entityClass = getClass(editFlow)
        def constraintViolations = buildDefaultValidatorFactory().getValidator()
                .validateValue(entityClass, editFlow.fieldName, covertToType(value, getFieldClass(entityClass, editFlow.fieldName)))
        if (!constraintViolations.findAll().isEmpty()) throw new ConstraintViolationException('Validation of input failed', constraintViolations)
    }

    Object covertToType(Object value, Class specificClass = null){
        String.valueOf('sdfs')
        value ? specificClass.valueOf(value) : null
    }

    void updateEntity(Object entity, String value, EditFlow editFlow) {
        log.trace 'Default updating entity with value {}', value
        entity?."${editFlow.fieldName}" = value
    }

    String allIncludedValuesAsString(EditFlow editFlow) {
        getPersistedValue(editFlow)?.toString()
    }

    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.editFlowSuccessMessage(editFlow.successMessage, clear)
        messages.updateEditFlowCurrentValueMessage(allIncludedValuesAsString(editFlow), editFlow.oldValue)
    }

    protected Object getPersistedValue(EditFlow editFlow) {
        if (!editFlow.entityId) return null
        entityManager.find(getClass(editFlow), editFlow.entityId)?."${editFlow.fieldName}"
    }
}
