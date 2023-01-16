package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.dto.EditEntriesFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntryFlowDto
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.service.EditFlowService
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import com.khabaznia.bots.core.meta.Emoji
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Input
import com.khabaznia.bots.core.service.BotMessagesService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.service.EditFlowService.isBooleanField
import static com.khabaznia.bots.core.flow.service.EditFlowService.isLocalizedField
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.*
import static com.khabaznia.bots.core.util.SessionUtil.currentUser
import static com.khabaznia.bots.core.util.SessionUtil.setRedirectParams

@Slf4j
@Component
@BotController
class EditFieldController extends AbstractBotController {

    @Autowired
    private FlowConversionUtil flowConversionUtil
    @Autowired
    private EditFlowService editFlowService
    @Autowired
    private BotMessagesService botMessagesService

    @BotRequest(path = EDIT_ENTRIES_FOR_CLASS_ENTER, rawParams = true)
    editEntitiesForClass(Map<String, String> params) {
        def editEntriesFlowDto = flowConversionUtil.getEditFieldFlowDto(EditEntriesFlowDto.class, params)
        log.debug 'Converted flow dto: {}', editEntriesFlowDto.toString()

    }

    @BotRequest(path = EDIT_ENTITY_ENTER, rawParams = true)
    editEntity(Map<String, String> params) {
        def editEntryFlowDto = flowConversionUtil.getEditFieldFlowDto(EditEntryFlowDto.class, params)
        def fields = editFlowService.getEditableFields(editEntryFlowDto.entityClass)
        botMessagesService.editFlowEntityFieldsSelectMessage(fields, editEntryFlowDto)
    }

    @BotRequest(path = EDIT_FIELD_ENTER, rawParams = true)
    editFieldEnter(Map<String, String> params) {
        def editFieldFlowDto = flowConversionUtil.getEditFieldFlowDto(EditFieldFlowDto.class, params)
        editFlowService.saveEditFlowModel(editFieldFlowDto)
        // send enter message (with back path, (POST MVP): options to check, print previous value)
        if (isLocalizedField(editFieldFlowDto.fieldName, editFieldFlowDto.entityClass)) {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.getCurrentValue(true))
            botMessagesService.editFlowChooseLangMessage()
        } else if (isBooleanField()) {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.currentValue, true)
            botMessagesService.editBooleanFieldMenu()
        } else {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.currentValue)
            botMessagesService.editFlowEnterMessage(editFieldFlowDto.enterText, editFieldFlowDto.enterTextBinding)
        }
    }

    @BotRequest(path = EDIT_LOCALIZED_FIELD_MENU, after = EDIT_FIELD_ENTER)
    editLocalizedField(String lang) {
        botMessagesService.updateEditFlowChooseLangMenu(lang)
        editFlowService.setFieldLang(lang)
        def editFlow = currentUser.editFlow
        botMessagesService.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    @BotRequest(path = EDIT_BOOLEAN_FIELD, after = EDIT_FIELD_ENTER)
    String editBooleanField(String value) { editFieldInternal(value) }

    @BotRequest(path = EDIT_FIELD_VALIDATION_FAILED)
    fieldValidationFailed() {
        sendMessage.text 'text.edit.flow.try.again'
    }

    @BotRequest(after = EDIT_FIELD_ENTER)
    String editFieldAction(@Input String input) { editFieldInternal(input) }

    @BotRequest(after = EDIT_LOCALIZED_FIELD_MENU)
    String editFieldActionAfterLocalized(@Input String input) { editFieldInternal(input) }

    @BotRequest(after = EDIT_FIELD_VALIDATION_FAILED)
    String addFieldAfterValidation(@Input String input) { editFieldInternal(input) }

    @BotRequest(path = EDIT_FIELD_CLEAR_VALUE, after = EDIT_LOCALIZED_FIELD_MENU)
    String clearLocalizedValue() { editFieldInternal() }

    @BotRequest(path = EDIT_FIELD_CLEAR_VALUE, after = EDIT_FIELD_ENTER)
    String clearValue() { editFieldInternal() }

    private String editFieldInternal(String input = null) {
        try {
            def editFlow = currentUser.editFlow
            editFlowService.updateEntityWithInput(input)
            botMessagesService.editFlowSuccessMessage(editFlow.successMessage, input == null)
            botMessagesService.deleteEditFlowChooseLangMessage()
            botMessagesService.updateEditFlowCurrentValueMessage(editFlowService.currentValue, isBooleanField())
            setRedirectParams([:] + editFlow.params)
            editFlowService.deleteOldFlow()
            return editFlow.successPath ?: Constants.COMMON.TO_MAIN
        } catch (ConstraintViolationException ex) {
            ex.constraintViolations*.messageTemplate.each { sendMessage.text(it) }
            return EDIT_FIELD_VALIDATION_FAILED
        }
    }
}
