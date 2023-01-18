package com.khabaznia.bots.core.flow.strategy

import com.khabaznia.bots.core.flow.util.EditFlowMessages
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.model.EditFlow
import com.khabaznia.bots.core.repository.EditFlowRepository
import com.khabaznia.bots.core.service.UserService
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getClass
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldType
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat
import static javax.validation.Validation.buildDefaultValidatorFactory

abstract class FieldProcessingStrategy {

    @Autowired
    private EditFlowRepository editFlowRepository
    @Autowired
    private UserService userService
    @Autowired
    protected EntityManager entityManager
    @Autowired
    protected EditFlowMessages messages

    void prepare(EditFlow editFlow) {
        def chat = currentChat
        deleteOldFlow(chat)

        editFlow.type = getFieldType(getClass(editFlow), editFlow.fieldName)
        editFlow.oldValue = currentValueAsString(editFlow)

        editFlowRepository.saveAndFlush(editFlow)
        chat.editFlow = editFlow
        userService.updateChat(chat)
    }

    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentValueMessage(currentValueAsString(editFlow))
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    void validate(EditFlow editFlow, String value) {
        def entityClass = getClass(editFlow)
        def constraintViolations = buildDefaultValidatorFactory().getValidator()
                .validateValue(entityClass, editFlow.fieldName, covertToType(value, getFieldClass(entityClass, editFlow.fieldName)))
        if (!constraintViolations.findAll().isEmpty()) throw new ConstraintViolationException('Validation of input failed', constraintViolations)
    }

    void updateEntity(Object entity, String value, EditFlow editFlow) {
        entity?."${editFlow.fieldName}" = value
    }

    String currentValueAsString(EditFlow editFlow) {
        getCurrentValueInternal(editFlow)?.toString()
    }

    abstract Object covertToType(Object value, Class specificClass = null)

    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.editFlowSuccessMessage(editFlow.successMessage, clear)
        messages.updateEditFlowCurrentValueMessage(currentValueAsString(editFlow), editFlow.oldValue)
    }

    protected Object getCurrentValueInternal(EditFlow editFlow) {
        if (!editFlow.entityId) return null
        entityManager.find(getClass(editFlow), editFlow.entityId)?."${editFlow.fieldName}"
    }

    private void deleteOldFlow(Chat chat) {
        def oldFlow = chat.editFlow
        if (oldFlow) {
            chat.editFlow = null
            userService.updateChat(chat)
            editFlowRepository.delete(oldFlow)
        }
    }

    protected static Class getFieldClass(Class entityClass, String fieldName) {
        entityClass.getDeclaredField(fieldName).type
    }
}
