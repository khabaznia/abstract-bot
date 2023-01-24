package com.khabaznia.bots.core.flow.strategy.impl.processing

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.flow.validation.MediaTypeValidator
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditFlowMessages.defaultEditFlowMediaEnterMessage
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.currentFieldAnnotation

@Slf4j
@Component('mediaFieldProcessingStrategy')
class MediaFieldProcessingStrategy extends FieldProcessingStrategy {

    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        if (!isNew) messages.editFlowCurrentMediaMessage(
                allIncludedValuesAsString(editFlow),
                currentFieldAnnotation.mediaType().correspondingBeanName)
        messages.editFlowEnterMessage(editFlow.enterText, editFlow.enterTextBinding,
                { defaultEditFlowMediaEnterMessage })
    }

    @Override
    void validate(EditFlow editFlow, String value) {
        if (value == null) return
        MediaTypeValidator.validate(editFlow.inputMediaType, currentFieldAnnotation.mediaType())
    }

    @Override
    String covertToType(Object value, Class specificClass) {
        value ?: value as String
    }

    @Override
    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.updateEditFlowCurrentMediaMessage(getPersistedValue(editFlow) as String, editFlow.oldValue)
        messages.mediaUpdatedSuccessMessage(editFlow.successMessage, clear)
    }
}
