package com.khabaznia.bots.core.flow.util

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

    public static final String ENTITY_CLASS_NAME = 'entityClassName'
    public static final String ENTITY_ID = 'entityId'
    private static final String REDIRECT_PARAMS_PREFIX = 'FLOW_REDIRECT_PARAM__'

    ConfirmationFlowDto getConfirmationFlowDto(Map<String, String> allParams) {
        def dto = fillDto(allParams, confirmationFlowDto)
        setParams(getRedirectParams(allParams), dto, 'redirectParams')
        setParams(allParams, dto, 'menuTextBinding')
    }

    def <T extends EditFlowDto> T getEditFieldFlowDto(Class<T> editFlowDtoClass, Map<String, String> allParams) {
        def dto = fillDto(allParams, get(editFlowDtoClass))
        setParams(getRedirectParams(allParams), dto, 'redirectParams')
        setParams(allParams, dto, 'enterTextBinding')
    }

    static InlineButton fillEditFlowButton(InlineButton inlineButton, String text, EditFlowDto editFlowDto, String emoji = null) {
        inlineButton.callbackData(getPath(editFlowDto))
                .params(populateParams(editFlowDto))
                .text(text)
                .emoji(emoji)
        inlineButton
    }

    static InlineButton fillConfirmationButton(InlineButton inlineButton, String text, ConfirmationFlowDto confirmationFlowDto, String emoji = null) {
        inlineButton.callbackData(CONFIRMATION_MENU)
                .params(confirmationFlowDto.getClass()
                        .declaredFields
                        .findAll { !it.synthetic }
                        .findAll { it.name != "redirectParams" }
                        .findAll { it.name != 'menuTextBinding' }
                        .collectEntries { field ->
                            [field.name, confirmationFlowDto."$field.name".toString()]
                        } << getMappedRedirectParams(confirmationFlowDto.redirectParams) << confirmationFlowDto.menuTextBinding)
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
        if (Boolean.class.isAssignableFrom(fieldClass))
            return Boolean.valueOf(value)
        if (Number.class.isAssignableFrom(fieldClass))
            return fieldClass.valueOf(value)
        value
    }

    private static <T> T setParams(Map<String, String> params, T dto, String fieldName) {
        dto."$fieldName"(params.findAll { !dto.hasProperty(it.key) })
    }

    private static String getPath(EditFlowDto editFlowDto) {
        switch (editFlowDto.class) {
            case (EditFieldFlowDto.class): return EDIT_FIELD_ENTER
            case (EditEntryFlowDto.class): return EDIT_ENTITY_ENTER
            case (EditEntriesFlowDto.class): return EDIT_ENTRIES_FOR_CLASS_ENTER
        }
        return null
    }

    private static Map<String, String> populateParams(EditFlowDto editFlowDto) {
        def resultParams = (editFlowDto.getClass().declaredFields + editFlowDto.getClass().getSuperclass().declaredFields)
                .findAll { !it.synthetic }
                .findAll { it.name != "entityToEdit" }
                .findAll { it.name != "entityClass" }
                .findAll { it.name != "redirectParams" }
                .findAll { it.name != 'enterTextBinding' }
                .collectEntries { field ->
                    [field.name, editFlowDto."$field.name".toString()]
                } as Map<String, String>
        resultParams << getMappedRedirectParams(editFlowDto.redirectParams)
        resultParams << editFlowDto.enterTextBinding
        switch (editFlowDto.class) {
            case (EditFieldFlowDto): populateEditEntryFlowDtoParams(resultParams, editFlowDto as EditFieldFlowDto); break
            case (EditEntryFlowDto): populateEditEntryFlowDtoParams(resultParams, editFlowDto as EditEntryFlowDto); break
            case (EditEntriesFlowDto): populateEditEntriesFlowDtoParams(resultParams, editFlowDto as EditEntriesFlowDto); break
        }
        resultParams
    }

    static void populateEditEntryFlowDtoParams(Map<String, String> resultParams, EditFieldFlowDto dto) {
        resultParams.put(ENTITY_CLASS_NAME, dto.entityToEdit.class.name)
        resultParams.put(ENTITY_ID, dto.entityToEdit.id.toString())
    }

    static void populateEditEntryFlowDtoParams(Map<String, String> resultParams, EditEntryFlowDto dto) {
        resultParams.put(ENTITY_CLASS_NAME, (dto.entityClass ?: dto.entityToEdit.class).name)
        resultParams.put(ENTITY_ID, dto.entityId ? dto.entityId.toString() : dto.entityToEdit.id.toString())
    }

    static void populateEditEntriesFlowDtoParams(Map<String, String> resultParams, EditEntriesFlowDto dto) {
        resultParams.put(ENTITY_CLASS_NAME, (dto.entityClass.name))
    }

    private static Map<String, String> getMappedRedirectParams(Map<String, String> params) {
        params.collectEntries { [(REDIRECT_PARAMS_PREFIX.concat(it.key)): it.value] } as Map<String, String>
    }

    private static Map<String, String> getRedirectParams(Map<String, String> allParams) {
        allParams.findAll { it.key.startsWith(REDIRECT_PARAMS_PREFIX) }
                .collectEntries { [(it.key.substring(REDIRECT_PARAMS_PREFIX.length())): it.value] }
    }

    private static String getNullableValue(String value) {
        value == 'null' ? null : value
    }
}
