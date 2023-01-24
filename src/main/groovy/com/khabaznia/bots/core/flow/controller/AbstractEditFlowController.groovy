package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.enums.MediaType
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

import static com.khabaznia.bots.core.controller.Constants.COMMON.*
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.FLOW_PARAM_PREFIX
import static com.khabaznia.bots.core.util.SessionUtil.setRedirectParams

@Slf4j
abstract class AbstractEditFlowController extends AbstractBotController {

    @Autowired
    protected FlowConversionUtil flowConversionUtil
    @Autowired
    protected EditFlowService editFlowService
    @Autowired
    protected EditFlowEntityService entityService
    @Autowired
    protected EditFlowMessages messages

    protected String editFieldInternal(String input = null) {
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

    protected void editLocalizedFieldInternal(String lang) {
        messages.updateEditFlowChooseLangMenu(lang)
        editFlowService.setFieldLang(lang)
        def editFlow = currentEditFlow
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding,)
    }

    protected void selectEntityInternal(String entityId) {
        editFlowService.selectEntityWithId(entityId)
        messages.updateSelectEntitiesMenu(entityService.getEntitiesToSelect())
    }

    protected selectFieldActionInternal(String editFlowId, Closure successFunction) {
        if (currentEditFlow.id.toString() != editFlowId) {
            messages.editFlowErrorMessage()
            messages.deleteSelectEntitiesFieldMenu()
            return TO_MAIN
        }
        successFunction.call()
    }
}
