package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.service.EditFlowService
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Input
import com.khabaznia.bots.core.service.BotMessagesService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*

import static com.khabaznia.bots.core.flow.service.EditFlowService.isBooleanField
import static com.khabaznia.bots.core.flow.service.EditFlowService.isLocalizedField
import static com.khabaznia.bots.core.flow.service.EditFlowService.getEntityEditableIdFieldName
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.*
import static com.khabaznia.bots.core.util.SessionUtil.currentChat
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
    @Qualifier('botMessagesService')
    private BotMessagesService messages

    @BotRequest(path = DELETE_ENTITY, rawParams = true)
    String deleteEntity(Map<String, String> params) {
        def deleteEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(DeleteEntityFlowDto.class, params)
        editFlowService.deleteEntity(deleteEntityFlowDto.entityClass, deleteEntityFlowDto.entityId)
        setRedirectParams(params)
        messages.deleteEntitySuccessMessage(deleteEntityFlowDto.successText)
        deleteEntityFlowDto.successPath
    }

    @BotRequest(path = CREATE_NEW_ENTITY, rawParams = true)
    createNewEntity(Map<String, String> params) {
        def createNewEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(CreateNewEntityFlowDto.class, params)
        params.put(FLOW_PARAM_PREFIX.concat('fieldName'), getEntityEditableIdFieldName(createNewEntityFlowDto.entityClass))
        params.newEntity = 'true'
        setRedirectParams(getPrefixUnmappedParams(params, REDIRECT_PARAMS_PREFIX))
        editFieldEnter(params)
    }

    @BotRequest(path = EDIT_ENTITY_ENTER, rawParams = true)
    editEntity(Map<String, String> params) {
        def editEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(EditEntityFlowDto.class, params)
        def fields = editFlowService.getEditableFields(editEntityFlowDto.entityClass)
        messages.editFlowEntityFieldsSelectMessage(fields, editEntityFlowDto)
    }

    @BotRequest(path = EDIT_FIELD_ENTER, rawParams = true)
    editFieldEnter(Map<String, String> params) {
        def isNew = Boolean.valueOf(params.newEntity)
        def editFieldFlowDto = flowConversionUtil.getEditFieldFlowDto(EditFieldFlowDto.class, params)
        editFlowService.saveEditFlowModel(editFieldFlowDto)
        // send enter message (with back path, (POST MVP): options to check, print previous value)
        if (isLocalizedField(editFieldFlowDto.fieldName, editFieldFlowDto.entityClass)) {
            messages.editFlowCurrentValueMessage(editFlowService.getCurrentValue(true), isNew)
            messages.editFlowChooseLangMessage()
        } else if (isBooleanField()) {
            messages.editFlowCurrentValueMessage(editFlowService.currentValue, isNew,true)
            messages.editBooleanFieldMenu()
        } else {
            messages.editFlowCurrentValueMessage(editFlowService.currentValue, isNew)
            messages.editFlowEnterMessage(editFieldFlowDto.enterText, editFieldFlowDto.enterTextBinding)
        }
    }

    @BotRequest(path = EDIT_LOCALIZED_FIELD_MENU, after = EDIT_FIELD_ENTER)
    editLocalizedField(String lang) { editLocalizedFieldInternal(lang) }

    @BotRequest(path = EDIT_LOCALIZED_FIELD_MENU, after = CREATE_NEW_ENTITY)
    editLocalizedFieldForNewEntry(String lang) { editLocalizedFieldInternal(lang) }

    @BotRequest(path = EDIT_BOOLEAN_FIELD, after = EDIT_FIELD_ENTER)
    String editBooleanField(String value) { editFieldInternal(value) }

    @BotRequest(path = EDIT_BOOLEAN_FIELD, after = CREATE_NEW_ENTITY)
    String editBooleanFieldForNewEntity(String value) { editFieldInternal(value) }

    @BotRequest(path = EDIT_FIELD_VALIDATION_FAILED)
    fieldValidationFailed() { sendMessage.text 'text.edit.flow.try.again' }

    @BotRequest(after = EDIT_FIELD_ENTER)
    String editFieldAction(@Input String input) { editFieldInternal(input) }

    @BotRequest(after = CREATE_NEW_ENTITY)
    String editFieldActionForNewEntity(@Input String input) { editFieldInternal(input) }

    @BotRequest(after = EDIT_LOCALIZED_FIELD_MENU)
    String editFieldActionAfterLocalized(@Input String input) { editFieldInternal(input) }

    @BotRequest(after = EDIT_FIELD_VALIDATION_FAILED)
    String addFieldAfterValidation(@Input String input) { editFieldInternal(input) }

    @BotRequest(path = EDIT_FIELD_CLEAR_VALUE, after = EDIT_LOCALIZED_FIELD_MENU)
    String clearLocalizedValue() { editFieldInternal() }

    @BotRequest(path = EDIT_FIELD_CLEAR_VALUE, after = CREATE_NEW_ENTITY)
    String clearValueAfterCreateNewEntity() { editFieldInternal() }

    @BotRequest(path = EDIT_FIELD_CLEAR_VALUE, after = EDIT_FIELD_ENTER)
    String clearValue() { editFieldInternal() }

    private void editLocalizedFieldInternal(String lang) {
        messages.updateEditFlowChooseLangMenu(lang)
        editFlowService.setFieldLang(lang)
        def editFlow = currentChat.editFlow
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    private String editFieldInternal(String input = null) {
        try {
            def editFlow = currentChat.editFlow
            editFlowService.updateEntityWithInput(input)
            messages.editFlowSuccessMessage(editFlow.successMessage, input == null)
            messages.deleteEditFlowChooseLangMessage(editFlow.lang)
            messages.updateEditFlowCurrentValueMessage(editFlowService.currentValue, isBooleanField())
            setRedirectParams([:] + editFlow.params)
            editFlowService.deleteOldFlow()
            return editFlow.successPath ?: TO_MAIN
        } catch (ConstraintViolationException ex) {
            ex.constraintViolations*.messageTemplate.each { sendMessage.text(it) }
            return EDIT_FIELD_VALIDATION_FAILED
        }
    }
}
