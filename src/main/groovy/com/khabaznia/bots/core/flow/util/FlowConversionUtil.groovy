package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.dto.*
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.trait.BaseRequests
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_ACTION
import static com.khabaznia.bots.core.controller.Constants.CONFIRMATION_CONTROLLER.CONFIRMATION_MENU
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.meta.Emoji.LEFT_ARROW
import static com.khabaznia.bots.core.meta.Emoji.THUMB_UP

@Component
class FlowConversionUtil implements BaseRequests {

    public static final String ENTITY_CLASS_NAME = 'entityClass'
    public static final String ENTITY_ID = 'entityId'
    public static final String REDIRECT_PARAMS_PREFIX = 'FLOW_REDIRECT_PARAM__'
    public static final String MESSAGE_BINDING_PARAMS_PREFIX = 'MESSAGE_BINDING_PARAM__'
    public static final String FLOW_PARAM_PREFIX = 'flow_param__'

    ConfirmationFlowDto getConfirmationFlowDto(Map<String, String> allParams) {
        allParams = getPrefixUnmappedParams(allParams, FLOW_PARAM_PREFIX)
        def dto = fillDto(allParams, confirmationFlowDto)
        dto.redirectParams(getPrefixUnmappedParams(allParams, REDIRECT_PARAMS_PREFIX))
        dto.menuTextBinding(getPrefixUnmappedParams(allParams, MESSAGE_BINDING_PARAMS_PREFIX))
    }

    def <T extends EditFlowDto> T getEditFieldFlowDto(Class<T> editFlowDtoClass, Map<String, String> allParams) {
        allParams = getPrefixUnmappedParams(allParams, FLOW_PARAM_PREFIX)
        def dto = fillDto(allParams, get(editFlowDtoClass))
        dto.redirectParams(getPrefixUnmappedParams(allParams, REDIRECT_PARAMS_PREFIX))
        dto.enterTextBinding(getPrefixUnmappedParams(allParams, MESSAGE_BINDING_PARAMS_PREFIX))
        dto
    }

    static InlineButton fillEditFlowButton(InlineButton inlineButton, String text, EditFlowDto editFlowDto, String emoji = null) {
        inlineButton.callbackData(getPath(editFlowDto))
                .params(getPrefixMappedParams(getEditFLowDtoParams(editFlowDto), FLOW_PARAM_PREFIX))
                .text(text ?: getFieldDefaultMessage(editFlowDto as EditFieldFlowDto))
                .emoji(emoji)
        inlineButton
    }

    static InlineButton fillConfirmationButton(InlineButton inlineButton, String text, ConfirmationFlowDto confirmationFlowDto, String emoji = null) {
        inlineButton.callbackData(CONFIRMATION_MENU)
                .params(getPrefixMappedParams(getConfirmationFlowDtoParams(confirmationFlowDto), FLOW_PARAM_PREFIX))
                .text(text)
                .emoji(emoji)
        inlineButton
    }

    InlineKeyboard getConfirmationKeyboard(ConfirmationFlowDto confirmationFlowDto) {
        def keyboard = inlineKeyboard
        keyboard.button(confirmationFlowDto.declinePathMessage ?: 'button.no', CONFIRMATION_ACTION,
                confirmationFlowDto.redirectParams << [nextPath: confirmationFlowDto.declinePath])
        keyboard.button(confirmationFlowDto.acceptPathMessage ?: 'button.yes', THUMB_UP, CONFIRMATION_ACTION,
                confirmationFlowDto.redirectParams << [nextPath: confirmationFlowDto.acceptPath])
        if (confirmationFlowDto.backPath) {
            keyboard.row()
            keyboard.button(confirmationFlowDto.backPathMessage ?: 'button.back', LEFT_ARROW, CONFIRMATION_ACTION,
                    confirmationFlowDto.redirectParams << [nextPath: confirmationFlowDto.backPath])
        }
        keyboard
    }

    private static <T> T fillDto(Map<String, String> params, T dto) {
        params.findAll { dto.hasProperty(it.key) }
                .each { dto.setProperty(it.key, getValue(it.key, getNullableValue(it.value), dto)) }
        return dto
    }

    private static Object getValue(String fieldName, String value, Object dto) {
        if (value == null) value
        // Get class of property: it can be either in this class or in super class
        def targetFieldClass = dto.class.getDeclaredFields()*.name.contains(fieldName)
                ? dto.class
                : dto.class.superclass
        // 'Cast' it to field class
        def fieldClass = targetFieldClass.getDeclaredField(fieldName).type
        if (Class.class.isAssignableFrom(fieldClass))
            return Class.forName(value)
        if (Boolean.class.isAssignableFrom(fieldClass))
            return Boolean.valueOf(value)
        if (Number.class.isAssignableFrom(fieldClass))
            return fieldClass.valueOf(value)
        value
    }

    private static String getPath(EditFlowDto editFlowDto) {
        switch (editFlowDto.class) {
            case (EditFieldFlowDto.class): return EDIT_FIELD_ENTER
            case (EditEntityFlowDto.class): return EDIT_ENTITY_ENTER
            case (DeleteEntityFlowDto.class): return DELETE_ENTITY
            case (CreateNewEntityFlowDto.class): return CREATE_NEW_ENTITY
        }
        return null
    }

    private static Map<String, String> getConfirmationFlowDtoParams(ConfirmationFlowDto confirmationFlowDto) {
        def resultParams = confirmationFlowDto.getClass()
                .declaredFields
                .findAll { !it.synthetic }
                .findAll { it.name != "redirectParams" }
                .findAll { it.name != 'menuTextBinding' }
                .collectEntries { field ->
                    [field.name, confirmationFlowDto."$field.name".toString()]
                } as Map<String, String>
        resultParams << redirectParams(confirmationFlowDto.redirectParams)
        resultParams << messageBindingParams(confirmationFlowDto.menuTextBinding)
        resultParams
    }

    static Map<String, String> getEditFLowDtoParams(EditFlowDto editFlowDto) {
        def resultParams = (editFlowDto.getClass().declaredFields + editFlowDto.getClass().getSuperclass().declaredFields)
                .findAll { !it.synthetic }
                .findAll { it.name != "entityToEdit" }
                .findAll { it.name != "entityClass" }
                .findAll { it.name != "redirectParams" }
                .findAll { it.name != 'enterTextBinding' }
                .collectEntries { field ->
                    [field.name, editFlowDto."$field.name".toString()]
                } as Map<String, String>
        resultParams << redirectParams(editFlowDto.redirectParams)
        resultParams << messageBindingParams(editFlowDto.enterTextBinding)
        populateEntityData(resultParams, editFlowDto)
        resultParams
    }

    static void populateEntityData(Map<String, String> resultParams, EditFlowDto dto) {
        resultParams.put(ENTITY_CLASS_NAME, getEditFlowDtoEntityClass(dto).name)
        if (dto.hasProperty('entityId'))
            resultParams.put(ENTITY_ID, dto.entityId ? dto.entityId.toString() : dto.entityToEdit.id.toString())
    }

    static Map<String, String> redirectParams(Map<String, String> params) {
        getPrefixMappedParams(params, REDIRECT_PARAMS_PREFIX)
    }

    static Map<String, String> messageBindingParams(Map<String, String> params) {
        getPrefixMappedParams(params, MESSAGE_BINDING_PARAMS_PREFIX)
    }

    static Map<String, String> getPrefixMappedParams(Map<String, String> params, String prefix) {
        params.collectEntries { [(prefix.concat(it.key)): it.value] } as Map<String, String>
    }

    static Map<String, String> getPrefixUnmappedParams(Map<String, String> allParams, String prefix) {
        allParams.findAll { it.key.startsWith(prefix) }
                .collectEntries { [(it.key.substring(prefix.length())): it.value] }
    }

    private static String getNullableValue(String value) {
        value == 'null' ? null : value
    }

    private static String getFieldDefaultMessage(EditFieldFlowDto dto) {
        getEditFlowDtoEntityClass(dto)
                ?.getDeclaredField(dto.fieldName)
                ?.getAnnotation(Editable.class)
                ?.fieldButtonMessage()
    }

    private static Class getEditFlowDtoEntityClass(EditFlowDto dto) {
        dto.entityClass ?: dto.entityToEdit.class
    }
}
