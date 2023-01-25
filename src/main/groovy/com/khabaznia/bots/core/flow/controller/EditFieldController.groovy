package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Input
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.FLOW_PARAM_PREFIX
import static com.khabaznia.bots.core.util.SessionUtil.setRedirectParams

@Slf4j
@Component
@BotController
class EditFieldController extends AbstractEditFlowController {

    @BotRequest(path = CREATE_NEW_ENTITY, rawParams = true)
    String createNewEntity(Map<String, String> params) {
        def createNewEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(CreateNewEntityFlowDto.class, params)
        params.put(FLOW_PARAM_PREFIX.concat('fieldName'), getEntityEditableIdFieldName(createNewEntityFlowDto.entityClass))
        log.debug 'Try to create new entity for class {{}}', createNewEntityFlowDto.entityClass?.simpleName
        params.newEntity = 'true'
        editFieldEnter(params)
    }

    @BotRequest(path = EDIT_ENTITY_ENTER, rawParams = true)
    editEntity(Map<String, String> params) {
        def editEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(EditEntityFlowDto.class, params)
        log.debug 'Enter edit entity menu for class {{}}, entity id {{}}.', editEntityFlowDto.entityClass, editEntityFlowDto.entityId
        def fields = getEditableFields(editEntityFlowDto.entityClass)
        messages.entityViewMessage(entityService.getEntityView(editEntityFlowDto))
        messages.editFlowEntityFieldsSelectMessage(fields, editEntityFlowDto)
    }

    @BotRequest(path = DELETE_ENTITY, rawParams = true)
    String deleteEntity(Map<String, String> params) {
        def deleteEntityFlowDto = flowConversionUtil.getEditFieldFlowDto(DeleteEntityFlowDto.class, params)
        log.info 'Try to delete entity of class {{}} for id {{}}', deleteEntityFlowDto.entityClass?.simpleName, deleteEntityFlowDto.entityId?.toString()
        entityService.deleteEntity(deleteEntityFlowDto.entityClass, deleteEntityFlowDto.entityId)
        setRedirectParams(params)
        messages.deleteEntitySuccessMessage(deleteEntityFlowDto.successText)
        deleteEntityFlowDto.successPath
    }

    @BotRequest(path = EDIT_FIELD_ENTER, rawParams = true)
    editFieldEnter(Map<String, String> params) {
        log.trace 'Enter edit flow process'
        def isNew = Boolean.valueOf(params?.newEntity)
        if (params.any { it.key.startsWith(FLOW_PARAM_PREFIX) }) {
            def editFieldFlowDto = flowConversionUtil.getEditFieldFlowDto(EditFieldFlowDto.class, params)
            editFlowService.saveEditFlowModel(editFieldFlowDto)
        }
        editFlowService.sendEnterMessage(isNew)
    }

    @BotRequest(path = EDIT_SELECTABLE_FIELD_AFTER_CREATE)
    String editSelectableFieldEnter(String entityId) {
        if (entityId)
            editFlowService.selectEntityWithId(entityId)
        EDIT_FIELD_ENTER
    }

    @BotRequest(path = EDIT_FIELD_CANCEL)
    String cancelEditField() {
        messages.editFieldCancelMessage()
        def editFlow = currentEditFlow
        editFlowService.postProcess(editFlow)
        return editFlow.successPath ?: TO_MAIN
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
}
