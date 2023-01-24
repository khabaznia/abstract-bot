package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.service.EditFlowEntityService
import com.khabaznia.bots.core.flow.service.EditFlowService
import com.khabaznia.bots.core.flow.util.EditFlowMessages
import com.khabaznia.bots.core.flow.util.FlowConversionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.EDIT_FIELD_VALIDATION_FAILED
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getCurrentEditFlow

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
            log.info 'Try to edit field {{}} for entity {{}} with input - {{}}', editFlow.fieldName, editFlow.id, input
            def entityId = editFlowService.updateEntityWithInput(input)
            editFlowService.sendSuccessMessages(editFlow, input == null)
            editFlowService.postProcess(editFlow, entityId)
            log.trace 'Edit flow finished successfully. Redirecting to success path'
            return editFlow.successPath ?: TO_MAIN
        } catch (ConstraintViolationException ex) {
            ex.constraintViolations*.messageTemplate.each {
                sendMessage.text(it)
                log.warn 'Constraint violation is occurred. Error: {}', it
            }
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
