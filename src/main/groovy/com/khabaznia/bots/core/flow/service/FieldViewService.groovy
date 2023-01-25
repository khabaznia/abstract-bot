package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.meta.Emoji
import com.khabaznia.bots.core.service.I18nService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Slf4j
@Component
class FieldViewService {

    @Autowired
    private Map<FieldType, FieldResolvingStrategy> fieldResolvingStrategyMap
    @Autowired
    private I18nService i18nService

    String getEntityViewHeader(Class entityClass, Long entityId) {
        def idFieldValue = getIdFieldValue(entityClass, entityId)
                ?: i18nService.getFilledTemplate('text.entity.view.value.empty.name')
        i18nService.getFilledTemplate(getEntityViewHeader(entityClass))
                .concat("<b>$idFieldValue</b>")
    }

    String getFieldStringView(Object entity, String fieldName, boolean ignoreEmpty) {
        def fieldAnnotation = getEditableAnnotationForField(entity.class, fieldName)
        def value = fieldResolvingStrategyMap.get(fieldAnnotation.type())
                .getStringView(entity, fieldName)
        if (ignoreEmpty && !value) return null
        def localizedFieldName = i18nService.getFilledTemplate(fieldAnnotation.fieldButtonMessage() ?: fieldName)
        "$localizedFieldName:    <b>${value ?: Emoji.NONE}</b>"
    }

    String getIdFieldValue(Class entityClass, Long entityId) {
        def idFieldType = getEntityIdFieldAnnotation(entityClass).type()
        fieldResolvingStrategyMap.get(idFieldType).getValue(entityClass,
                entityId, getEntityEditableIdFieldName(entityClass))
    }
}
