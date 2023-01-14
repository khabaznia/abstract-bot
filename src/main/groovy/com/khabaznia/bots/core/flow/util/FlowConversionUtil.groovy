package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.flow.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.flow.dto.EditFlowDto
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

    ConfirmationFlowDto getConfirmationFlowDto(Map<String, String> params) {
        def dto = fillDto(params, confirmationFlowDto)
        setParams(params, dto, 'params')
        setParams(params, dto, 'menuTextBinding')
    }

    EditFlowDto getEditFlowDto(Map<String, String> params) {
        def dto = fillDto(params, editFlowDto)
        setParams(params, dto, 'params')
        setParams(params, dto, 'enterTextBinding')
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
                        .findAll { it.name != "params" }
                        .findAll { it.name != 'menuTextBinding' }
                        .collectEntries { field ->
                            [field.name, confirmationFlowDto."$field.name"]
                        } << confirmationFlowDto.params << confirmationFlowDto.menuTextBinding)
                .text(text)
                .emoji(emoji)
        inlineButton
    }

    InlineKeyboard getConfirmationKeyboard(ConfirmationFlowDto confirmationFlowDto) {
        def keyboard = inlineKeyboard
        keyboard.button(confirmationFlowDto.declinePathMessage ?: 'button.no', CONFIRMATION_ACTION,
                confirmationFlowDto.params << [nextPath: confirmationFlowDto.declinePath])
        keyboard.button(confirmationFlowDto.acceptPathMessage ?: 'button.yes', THUMB_UP, CONFIRMATION_ACTION,
                confirmationFlowDto.params << [nextPath: confirmationFlowDto.acceptPath])
        if (confirmationFlowDto.backPath) {
            keyboard.row()
            keyboard.button(confirmationFlowDto.backPathMessage ?: 'button.back', LEFT_ARROW, CONFIRMATION_ACTION,
                    confirmationFlowDto.params << [nextPath: confirmationFlowDto.backPath])
        }
        keyboard
    }

    private static <T> T fillDto(Map<String, String> params, T dto) {
        params.findAll { dto.hasProperty(it.key) }
                .each { dto.setProperty(it.key, (it.value == 'null' ? null : it.value)) }
        return dto
    }

    private static <T> T setParams(Map<String, String> params, T dto, String fieldName) {
        dto."$fieldName"(params.findAll { !dto.hasProperty(it.key) })
    }

    private static String getPath(EditFlowDto editFlowDto) {
        if (editFlowDto.entityToEdit && editFlowDto.fieldName)
            return EDIT_FIELD_ENTER
        if (editFlowDto.entityToEdit)
            return EDIT_ENTITY_ENTER
        if (editFlowDto.entityClass)
            return EDIT_ENTITIES_FOR_CLASS_ENTER
        return null
    }

    private static Map<String, String> populateParams(EditFlowDto editFlowDto) {
        def resultParams = editFlowDto.getClass()
                .declaredFields
                .findAll { !it.synthetic }
                .findAll { it.name != "entityToEdit" }
                .findAll { it.name != "entityClass" }
                .findAll { it.name != "params" }
                .findAll { it.name != 'enterTextBinding' }
                .collectEntries { field ->
                    [field.name, editFlowDto."$field.name"]
                } as Map<String, String>
        resultParams << editFlowDto.params
        resultParams << editFlowDto.enterTextBinding
        resultParams.put(ENTITY_CLASS_NAME, getEntityClassName(editFlowDto))
        if (editFlowDto.entityToEdit)
            resultParams.put(ENTITY_ID, editFlowDto.entityToEdit.id.toString())
        resultParams
    }

    private static String getEntityClassName(EditFlowDto editFlowDto) {
        (editFlowDto.entityClass ?: editFlowDto.entityToEdit.class).name
    }
}
