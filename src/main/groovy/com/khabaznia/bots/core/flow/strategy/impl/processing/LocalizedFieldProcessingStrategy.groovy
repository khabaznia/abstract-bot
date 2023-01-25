package com.khabaznia.bots.core.flow.strategy.impl.processing

import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static java.lang.System.lineSeparator

@Slf4j
@Component('localizedFieldProcessingStrategy')
class LocalizedFieldProcessingStrategy extends FieldProcessingStrategy implements Configurable {

    @Override
    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentValueMessage(allIncludedValuesAsString(editFlow))
        messages.editFlowChooseLangMessage(editFlow.enterText, editFlow.enterTextBinding)
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
    void validate(EditFlow editFlow, String value) {
        // no validation
    }

    @Override
    String allIncludedValuesAsString(EditFlow editFlow) {
        def localizedValues = getPersistedValue(editFlow)
        if (!localizedValues) return null
        lineSeparator().concat((localizedValues as Map<String, String>)
                .collectEntries { [(Constants.LANG_CONTROLLER.LANG_EMOJI.get(it.key)): it.value] }
                .collect { "$it.key  $it.value" }
                .join(lineSeparator()))
    }
}
