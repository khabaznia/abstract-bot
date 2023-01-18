package com.khabaznia.bots.core.flow.strategy

import com.khabaznia.bots.core.model.EditFlow
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.LANG_CONTROLLER.LANG_EMOJI
import static java.lang.System.lineSeparator

@Slf4j
@Component('localizedFieldProcessingStrategy')
class LocalizedFieldProcessingStrategy extends FieldProcessingStrategy {

    @Override
    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentValueMessage(currentValueAsString(editFlow))
        messages.editFlowChooseLangMessage()
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        entity?."${editFlow.fieldName}"["${editFlow.lang}"] = value
    }

    @Override
    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.deleteEditFlowChooseLangMessage()
        super.sendSuccessMessages(editFlow, clear)
    }

    @Override
    String currentValueAsString(EditFlow editFlow) {
        def stringValue = getCurrentValueInternal(editFlow)
        if (!stringValue) return null
        lineSeparator().concat((stringValue as Map<String, String>)
                .collectEntries { [(LANG_EMOJI.get(it.key)): it.value] }
                .collect { "$it.key  $it.value" }
                .join(lineSeparator()))
    }

    @Override
    String covertToType(Object value, Class specificClass) {
        value.toString()
    }
}
