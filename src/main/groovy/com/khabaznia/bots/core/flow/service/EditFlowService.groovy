package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.dto.EditFlowDto
import com.khabaznia.bots.core.flow.validation.InputNumberValidator
import com.khabaznia.bots.core.model.EditFlow
import com.khabaznia.bots.core.repository.EditFlowRepository
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.support.Repositories
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.annotation.PostConstruct
import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.LANG_CONTROLLER.LANG_EMOJI
import static com.khabaznia.bots.core.util.SessionUtil.currentUser
import static java.lang.System.lineSeparator
import static javax.validation.Validation.buildDefaultValidatorFactory

@Slf4j
@Service
@Validated
class EditFlowService {

    private Repositories repositories
    @Autowired
    private ApplicationContext context
    @Autowired
    private EditFlowRepository editFlowRepository
    @Autowired
    private UserService userService

    @PostConstruct
    private void postConstruct() {
        repositories = new Repositories(context)
    }

    void saveEditFlowModel(String entityClassName, String entityId, EditFlowDto editFlowDto) {
        deleteOldFlow()
        def user = currentUser
        user.editFlow = new EditFlow(
                entityClassName: entityClassName,
                entityId: Long.valueOf(entityId),
                fieldName: editFlowDto.fieldName,
                enterText: editFlowDto.enterText,
                enterTextBinding: editFlowDto.enterTextBinding,
                successMessage: editFlowDto.successText,
                successPath: editFlowDto.successPath
        )
        userService.updateUser(user)
    }

    void updateEntityWithInput(String input) {
        validateInput(input, currentUser.editFlow.fieldName)
        def entity = getFilledEntity(input)
        getRepository(entity.class).save(entity)
    }

    void setFieldLang(String lang) {
        def editFlow = currentUser.editFlow
        editFlow.setLang(lang)
        editFlowRepository.save(editFlow)
    }

    void deleteOldFlow() {
        def user = currentUser
        def oldDraft = user.editFlow
        if (oldDraft) {
            user.editFlow = null
            userService.updateUser(user)
            editFlowRepository.delete(oldDraft)
        }
    }

    static boolean isLocalizedField(String fieldName, String entityClassName) {
        Class.forName(entityClassName).getDeclaredField(fieldName)
                .getAnnotation(Editable.class).localized()
    }

    static boolean isBooleanField() {
        def editFlow = currentUser.editFlow
        Class.forName(editFlow.entityClassName).getDeclaredField(editFlow.fieldName)
                .type.isAssignableFrom(Boolean.class)
    }

    static String getEnterMessage() {
        entityClass.getDeclaredField(currentUser.editFlow.fieldName)
                .getAnnotation(Editable.class).enterMessage()
    }

    String getCurrentValue(boolean localized = false) {
        def editFlow = currentUser.editFlow
        isLocalized(editFlow) || localized
                ? lineSeparator().concat(getLocalizedCurrentValues(editFlow))
                : getCurrentValueInternal(editFlow)?.toString()
    }


    private static validateInput(String input, String fieldName) {
        validateNumberField(fieldName, input)
        def constraintViolations =
                buildDefaultValidatorFactory().getValidator().validateValue(entityClass, fieldName, getClassSpecificValue(input, fieldName))
        if (!constraintViolations.findAll().isEmpty())
            throw new ConstraintViolationException('Validation of input failed', constraintViolations)
    }

    private Object getFilledEntity(String input) {
        def editFlow = currentUser.editFlow
        def entity = getRepository(entityClass).getById(editFlow.entityId)
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
        if (isNumber(fieldName))
            return getNumberValue(fieldName, input)
        else if (isBoolean(fieldName))
            return Boolean.valueOf(input)
        else
            return input
    }

    private static void validateNumberField(String fieldName, String input) {
        if (isNumber(fieldName))
            InputNumberValidator.validate(input, getFieldClass(fieldName))
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
        entityClass.getDeclaredField(fieldName).type
    }

    private String getLocalizedCurrentValues(EditFlow editFlow) {
        (getCurrentValueInternal(editFlow) as Map<String, String>)
                .collectEntries { [(LANG_EMOJI.get(it.key)): it.value] }
                .collect { "$it.key  $it.value" }
                .join(lineSeparator())
    }

    private Object getCurrentValueInternal(EditFlow editFlow) {
        getRepository(entityClass).getById(editFlow.entityId)."${editFlow.fieldName}"
    }

    private static Class<?> getEntityClass() {
        Class.forName(currentUser.editFlow.entityClassName)
    }

    JpaRepository getRepository(Class entityClass) {
        repositories.getRepositoryFor(entityClass).get() as JpaRepository
    }
}
