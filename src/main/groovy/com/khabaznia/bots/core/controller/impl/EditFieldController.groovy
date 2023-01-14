package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.service.EditFlowService
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.service.BotMessagesService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.service.EditFlowService.isBooleanField
import static com.khabaznia.bots.core.flow.service.EditFlowService.isLocalizedField
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.ENTITY_CLASS_NAME
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.ENTITY_ID
import static com.khabaznia.bots.core.util.SessionUtil.currentUser

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

    @BotRequest(path = EDIT_ENTITIES_FOR_CLASS_ENTER, rawParams = true)
    editEntitiesForClass(Map<String, String> params) {
        // TODO
    }

    @BotRequest(path = EDIT_ENTITY_ENTER, rawParams = true)
    editEntity() {

    }

    @BotRequest(path = EDIT_FIELD_ENTER, rawParams = true)
    editFieldEnter(Map<String, String> params) {
        def editFlowDto = flowConversionUtil.getEditFlowDto(params)
        editFlowService.saveEditFlowModel(params.get(ENTITY_CLASS_NAME), params.get(ENTITY_ID), editFlowDto)
        // send enter message (with back path, (POST MVP): options to check, print previous value)
        if (isLocalizedField(editFlowDto.fieldName, params.get(ENTITY_CLASS_NAME))) {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.getCurrentValue(true))
            botMessagesService.editFlowChooseLangMessage()
        } else if (isBooleanField()) {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.currentValue, true)
            botMessagesService.editBooleanFieldMenu()
        } else {
            botMessagesService.editFlowCurrentValueMessage(editFlowService.currentValue)
            botMessagesService.editFlowEnterMessage(editFlowDto.enterText, editFlowDto.enterTextBinding)
        }
    }

    @BotRequest(path = EDIT_BOOLEAN_FIELD, after = EDIT_FIELD_ENTER)
    editBooleanField(String value) {
        editFieldInternal(value)
    }

    @BotRequest(path = EDIT_LOCALIZED_FIELD_MENU, after = EDIT_FIELD_ENTER)
    editLocalizedField(String lang) {
        botMessagesService.updateEditFlowChooseLangMenu(lang)
        editFlowService.setFieldLang(lang)
        def editFlow = currentUser.editFlow
        botMessagesService.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding)
    }

    @BotRequest(path = EDIT_FIELD_VALIDATION_FAILED)
    fieldValidationFailed() {
        sendMessage.text 'text.edit.flow.try.again'
    }

    @BotRequest(after = EDIT_FIELD_ENTER)
    String editFieldAction() { editFieldInternal() }

    @BotRequest(after = EDIT_LOCALIZED_FIELD_MENU)
    String editFieldActionAfterLocalized() { editFieldInternal() }

    @BotRequest(after = EDIT_FIELD_VALIDATION_FAILED)
    String addFieldAfterValidation() { editFieldInternal() }

    private String editFieldInternal(String input = null) {
        try {
            def editFlow = currentUser.editFlow
            input = input ?: updateService.getMappedMessageText(update)
            editFlowService.updateEntityWithInput(input)
            botMessagesService.editFlowSuccessMessage(editFlow.successMessage)
            botMessagesService.deleteEditFlowChooseLangMessage()
            botMessagesService.updateEditFlowCurrentValueMessage(editFlowService.currentValue, isBooleanField())
            editFlowService.deleteOldFlow()
            return editFlow.successPath ?: Constants.COMMON.TO_MAIN
        } catch (ConstraintViolationException ex) {
            ex.constraintViolations*.messageTemplate.each { sendMessage.text(it) }
            return EDIT_FIELD_VALIDATION_FAILED
        }
    }
}
