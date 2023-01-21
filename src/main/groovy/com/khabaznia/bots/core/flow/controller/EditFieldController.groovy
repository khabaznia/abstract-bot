package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.service.EditFlowEntityService
import com.khabaznia.bots.core.flow.service.EditFlowService
import com.khabaznia.bots.core.flow.util.EditFlowMessages
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Input
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.FLOW_PARAM_PREFIX
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
    private EditFlowEntityService entityService
    @Autowired
    private EditFlowMessages messages

    @BotRequest(path = CREATE_NEW_ENTITY, rawParams = true)
    String createNewEntity(Map<String, String> params) {
        def createNewEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(CreateNewEntityFlowDto.class, params)
        params.put(FLOW_PARAM_PREFIX.concat('fieldName'), getEntityEditableIdFieldName(createNewEntityFlowDto.entityClass))
        params.newEntity = 'true'
        editFieldEnter(params)
    }

    @BotRequest(path = EDIT_ENTITY_ENTER, rawParams = true)
    editEntity(Map<String, String> params) {
        def editEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(EditEntityFlowDto.class, params)
        def fields = getEditableFields(editEntityFlowDto.entityClass)
        messages.editFlowEntityFieldsSelectMessage(fields, editEntityFlowDto)
    }

    @BotRequest(path = DELETE_ENTITY, rawParams = true)
    String deleteEntity(Map<String, String> params) {
        def deleteEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(DeleteEntityFlowDto.class, params)
        entityService.deleteEntity(deleteEntityFlowDto.entityClass, deleteEntityFlowDto.entityId)
        setRedirectParams(params)
        messages.deleteEntitySuccessMessage(deleteEntityFlowDto.successText)
        deleteEntityFlowDto.successPath
    }

    @BotRequest(path = EDIT_FIELD_ENTER, rawParams = true)
    editFieldEnter(Map<String, String> params) {
        def isNew = Boolean.valueOf(params?.newEntity)
        if (params.any { it.key.startsWith(FLOW_PARAM_PREFIX) }) {
            def editFieldFlowDto = flowConversionUtil.getEditFieldFlowDto(EditFieldFlowDto.class, params)
            editFlowService.saveEditFlowModel(editFieldFlowDto)
        }
        editFlowService.sendEnterMessage(isNew)
    }

    @BotRequest(path = EDIT_SELECTABLE_FIELD_AFTER_CREATE)
    String editSelectableFieldEnter(String entityId) {
        editFlowService.selectEntityWithId(entityId)
        EDIT_FIELD_ENTER
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

    @BotRequest(path = SELECT_ENTITIES_CONFIRM, after = EDIT_FIELD_ENTER)
    String selectEntitiesConfirm(String editFlowId) { selectFieldActionInternal(editFlowId, this::editFieldInternal) }

    @BotRequest(path = SELECT_ENTITIES_CONFIRM, after = SELECT_ENTITY_COLLECTION_FIELD)
    String selectEntitiesConfirmAfterSelect(String editFlowId) {
        selectFieldActionInternal(editFlowId, this::editFieldInternal)
    }

    @BotRequest(path = SELECT_ENTITY_COLLECTION_FIELD, after = SELECT_ENTITY_COLLECTION_FIELD)
    selectEntityCollectionFieldAfterSelect(String entityId, String editFlowId) {
        selectFieldActionInternal(editFlowId, { selectEntityInternal(entityId) })
    }

    @BotRequest(path = SELECT_ENTITY_COLLECTION_FIELD, after = EDIT_FIELD_ENTER)
    selectEntityCollectionField(String entityId, String editFlowId) {
        selectFieldActionInternal(editFlowId, { selectEntityInternal(entityId) })
    }

    private String editFieldInternal(String input = null) {
        try {
            def editFlow = currentEditFlow
            def entityId = editFlowService.updateEntityWithInput(input)
            editFlowService.sendSuccessMessages(editFlow, input == null)
            editFlowService.postProcess(editFlow, entityId)
            return editFlow.successPath ?: TO_MAIN
        } catch (ConstraintViolationException ex) {
            ex.constraintViolations*.messageTemplate.each { sendMessage.text(it) }
            return EDIT_FIELD_VALIDATION_FAILED
        }
    }

    private void editLocalizedFieldInternal(String lang) {
        messages.updateEditFlowChooseLangMenu(lang)
        editFlowService.setFieldLang(lang)
        def editFlow = currentEditFlow
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    private void selectEntityInternal(String entityId) {
        editFlowService.selectEntityWithId(entityId)
        messages.updateSelectEntitiesMenu(entityService.getEntitiesToSelect())
    }

    private selectFieldActionInternal(String editFlowId, Closure successFunction) {
        if (currentEditFlow.id.toString() != editFlowId) {
            messages.editFlowErrorMessage()
            messages.deleteSelectEntitiesFieldMenu()
            return TO_MAIN
        }
        successFunction.call()
    }
}
