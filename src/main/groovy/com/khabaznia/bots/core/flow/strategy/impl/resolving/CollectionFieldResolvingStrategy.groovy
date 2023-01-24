package com.khabaznia.bots.core.flow.strategy.impl.resolving

import com.khabaznia.bots.core.flow.service.FieldViewService
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.service.I18nService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getSelectableFieldEntityClass

@Component('collectionFieldResolvingStrategy')
class CollectionFieldResolvingStrategy extends FieldResolvingStrategy {

    @Lazy
    @Autowired
    private FieldViewService fieldViewService
    @Autowired
    private I18nService i18nService

    Object getValue(Class entityClass, Long entityId, String fieldName) {
        def fieldClass = getFieldClass(entityClass, fieldName)
        fieldClass.valueOf(getPersistedValue(entityClass, entityId, fieldName))
    }

    @Override
    String getStringView(Object entity, String fieldName) {
        def collection = entity."$fieldName" as Collection
        if (collection == null || collection.isEmpty()) return null
        collection.collect {
            fieldViewService.getIdFieldValue(getSelectableFieldEntityClass(entity.class, fieldName), Long.valueOf(it.id))
                    ?: i18nService.getFilledTemplate('text.entity.view.value.empty.name')
        }.join(', ')
    }
}
