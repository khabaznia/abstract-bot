package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.validation.InputNumberValidator
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.model.EditFlow
import com.khabaznia.bots.core.model.User
import com.khabaznia.bots.core.repository.EditFlowRepository
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.LANG_CONTROLLER.LANG_EMOJI
import static com.khabaznia.bots.core.util.SessionUtil.currentChat
import static java.lang.System.lineSeparator
import static javax.validation.Validation.buildDefaultValidatorFactory

@Slf4j
@Service
@Validated
@Transactional
class EditFlowService {

    @Autowired
    private EntityManager entityManager
    @Autowired
    private ApplicationContext context
    @Autowired
    private EditFlowRepository editFlowRepository
    @Autowired
    private UserService userService

    void saveEditFlowModel(EditFieldFlowDto editFieldFlowDto) {
        deleteOldFlow()
        def chat = currentChat
        def newEditFlowModel = new EditFlow(entityClassName: editFieldFlowDto.entityClass.name,
                entityId: editFieldFlowDto.entityId,
                fieldName: editFieldFlowDto.fieldName,
                enterText: editFieldFlowDto.enterText,
                enterTextBinding: editFieldFlowDto.enterTextBinding,
                successMessage: editFieldFlowDto.successText,
                successPath: editFieldFlowDto.successPath,
                params: editFieldFlowDto.redirectParams)
        saveOldValue(editFieldFlowDto, newEditFlowModel, chat)
        userService.updateChat(chat)
    }

    void updateEntityWithInput(String input) {
        validateInput(input, currentChat.editFlow.fieldName)
        def entity = getFilledEntity(input)
        entityManager.persist(entity)
        entityManager.flush()
    }

    void setFieldLang(String lang) {
        def editFlow = currentChat.editFlow
        editFlow.setLang(lang)
        editFlowRepository.save(editFlow)
    }

    void deleteOldFlow() {
        def chat = currentChat
        def oldDraft = chat.editFlow
        if (oldDraft) {
            chat.editFlow = null
            userService.updateChat(chat)
            editFlowRepository.delete(oldDraft)
        }
    }

    String getCurrentValue(boolean localized = false, EditFlow editFlow = null) {
        editFlow = editFlow ?: currentChat.editFlow
        isLocalized(editFlow) || localized
                ? getLocalizedCurrentValue(editFlow)
                : getCurrentValueInternal(editFlow)?.toString()
    }

    void deleteEntity(Class entityClass, Long entityId) {
        def entity = entityManager.find(entityClass, entityId)
        entityManager.remove(entity)
    }

    static boolean isLocalizedField(String fieldName, Class entityClass) {
        entityClass.getDeclaredField(fieldName)
                .getAnnotation(Editable.class).localized()
    }

    static boolean isBooleanField() {
        def editFlow = currentChat.editFlow
        Class.forName(editFlow.entityClassName).getDeclaredField(editFlow.fieldName)
                .type.isAssignableFrom(Boolean.class)
    }

    static String getEnterMessage() {
        getEntityClass().getDeclaredField(currentChat.editFlow.fieldName)
                .getAnnotation(Editable.class).enterMessage()
    }

    static boolean isValueClearingEnabled() {
        getEntityClass().getDeclaredField(currentChat.editFlow.fieldName)
                .getAnnotation(Editable.class).enableClear()
    }

    static Map<String, String> getEditableFields(Class entityClass) {
        entityClass.getDeclaredFields()
                .findAll { it.getAnnotation(Editable.class) != null }
                .collectEntries { [(it.name): (it.getAnnotation(Editable.class).fieldButtonMessage() ?: it.name)] }
    }

    static String getEntityEditableIdFieldName(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
                .name
    }

    static String getDefaultMessageOfIdField(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
                .getAnnotation(Editable.class).fieldButtonMessage()
    }

    private void saveOldValue(EditFieldFlowDto editFieldFlowDto, EditFlow editFlowModel, Chat chat) {
        def oldValue = getCurrentValue(isLocalizedField(editFieldFlowDto.fieldName, editFieldFlowDto.entityClass), editFlowModel)
        editFlowModel.oldValue = oldValue
        chat.editFlow = editFlowModel
    }

    private static validateInput(String input, String fieldName) {
        validateNumberField(fieldName, input)
        def constraintViolations =
                buildDefaultValidatorFactory().getValidator().validateValue(getEntityClass(), fieldName, getClassSpecificValue(input, fieldName))
        if (!constraintViolations.findAll().isEmpty()) throw new ConstraintViolationException('Validation of input failed', constraintViolations)
    }

    private Object getFilledEntity(String input) {
        def editFlow = currentChat.editFlow
        def entityClass = getEntityClass(editFlow)
        def entity = editFlow.entityId
                ? entityManager.find(entityClass, editFlow.entityId)
                : entityClass.getDeclaredConstructor().newInstance()
        fillEntity(input, entity, editFlow)
        entity
    }

    private static void fillEntity(String input, entity, EditFlow editFlow) {
        def fieldName = editFlow.fieldName
        if (isLocalized(editFlow))
            entity?."${fieldName}"["${editFlow.lang}"] = input
        else
            entity?."${fieldName}" = getClassSpecificValue(input, fieldName)
    }

    private static Object getClassSpecificValue(String input, String fieldName) {
        if (isNumber(fieldName)) return getNumberValue(fieldName, input) else if (isBoolean(fieldName)) return Boolean.valueOf(input) else return input
    }

    private static void validateNumberField(String fieldName, String input) {
        if (isNumber(fieldName)) InputNumberValidator.validate(input, getFieldClass(fieldName))
    }

    private static getNumberValue(String fieldName, String value) {
        def fieldClass = getFieldClass(fieldName)
        fieldClass.valueOf(value)
    }

    private static boolean isNumber(String fieldName) {
        def fieldClass = getFieldClass(fieldName)
        Number.class.isAssignableFrom(fieldClass)
    }

    private static boolean isBoolean(String fieldName) {
        def fieldClass = getFieldClass(fieldName)
        Boolean.class.isAssignableFrom(fieldClass)
    }

    private static boolean isLocalized(EditFlow editFlow) {
        editFlow.lang
    }

    private static Class getFieldClass(String fieldName) {
        getEntityClass().getDeclaredField(fieldName).type
    }

    private String getLocalizedCurrentValue(EditFlow editFlow) {
        def stringValue = getCurrentValueInternal(editFlow)
        if (!stringValue) return null
        lineSeparator().concat((stringValue as Map<String, String>)
                .collectEntries { [(LANG_EMOJI.get(it.key)): it.value] }
                .collect { "$it.key  $it.value" }
                .join(lineSeparator()))
    }

    private Object getCurrentValueInternal(EditFlow editFlow) {
        if (!editFlow.entityId) return null
        entityManager.find(getEntityClass(editFlow), editFlow.entityId)?."${editFlow.fieldName}"
    }

    private static Class<?> getEntityClass(EditFlow editFlow = null) {
        Class.forName((editFlow ?: currentChat.editFlow).entityClassName)
    }
}
