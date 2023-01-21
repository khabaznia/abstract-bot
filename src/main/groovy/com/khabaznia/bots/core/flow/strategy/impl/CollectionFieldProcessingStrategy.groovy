package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.service.EditFlowEntityService
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Slf4j
@Component('collectionFieldProcessingStrategy')
class CollectionFieldProcessingStrategy extends FieldProcessingStrategy {

    @Autowired
    private EditFlowEntityService entityService

    @Override
    void fillOldValue(EditFlow editFlow) {
        editFlow.initialIds = getPersistedValue(editFlow)*.id
        editFlow.selectedIds = getPersistedValue(editFlow)*.id
    }

    @Override
    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        messages.editFlowSelectEntitiesMenu(editFlow.enterText, editFlow.enterTextBinding,
                entityService.getEntitiesToSelect())
    }

    @Override
    void validate(EditFlow editFlow, String value) {
        // no validation
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        def entitiesToSave = editFlow.selectedIds - editFlow.initialIds
        def entitiesToRemove = editFlow.initialIds - editFlow.selectedIds
        updateReferencesInChildEntities(entitiesToSave.unique(), entity, false)
        updateReferencesInChildEntities(entitiesToRemove.unique(), entity, true)
        entity."$editFlow.fieldName" = getSelectedEntities(editFlow.selectedIds)
    }

    private void updateReferencesInChildEntities(List<Long> ids, Object entity, boolean isRemove) {
        def entitiesToRemove = getSelectedEntities(ids)
        entitiesToRemove?.each {
            def fieldName = currentFieldAnnotation.mappedBy()
            selectableFieldHIsManyToManyRelation
                    ? (isRemove ? (it."$fieldName"?.remove(it)) : (it."$fieldName"?.add(entity)))
                    : (isRemove ? (it."$fieldName" = null) : (it."$fieldName" = entity))
        }
    }

    private List<?> getSelectedEntities(List<Long> ids) {
        entityManager
                .createQuery("SELECT e FROM $selectableFieldHTableName e WHERE e.id IN :idsList", selectableFieldEntityClass)
                .setParameter('idsList', ids)
                .resultList
    }

    @Override
    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.deleteSelectEntitiesFieldMenu()
        messages.selectedEntitiesSavedMessage()
    }

    @Override
    Object covertToType(Object value, Class specificClass) {
        throw new UnsupportedOperationException('Localized field can\'t be converted')
    }
}
