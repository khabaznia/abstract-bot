package com.khabaznia.bots.core.flow.strategy.impl.processing

import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.service.EditFlowEntityService
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Slf4j
@Component('collectionFieldProcessingStrategy')
class CollectionFieldProcessingStrategy extends FieldProcessingStrategy {

    @Autowired
    private EditFlowEntityService entityService
    @Autowired
    private ApplicationContext context

    @Override
    void fillOldValue(EditFlow editFlow) {
        editFlow.initialIds = getPersistedValue(editFlow)*.id
        editFlow.selectedIds = getPersistedValue(editFlow)*.id
    }

    @Override
    void sendEnterMessages(EditFlow editFlow, boolean isNew) {
        messages.editFlowSelectEntitiesMenu(editFlow.enterText, editFlow.enterTextBinding,
                entityService.entitiesToSelect)
    }

    @Override
    void validate(EditFlow editFlow, String value) {
        // no validation
    }

    @Override
    void updateEntity(Object entity, String value, EditFlow editFlow) {
        def strategy = fieldSelectionStrategy
        def entitiesToSave = editFlow.selectedIds - editFlow.initialIds
        def entitiesToRemove = editFlow.initialIds - editFlow.selectedIds
        log.trace 'Entities to save -> {}', entitiesToSave
        log.trace 'Entities to remove -> {}', entitiesToRemove
        log.trace 'Resolved selection strategy: {}', fieldSelectionStrategy?.class?.simpleName
        strategy.selectEntities(entity, entitiesToSave)
        strategy.removeEntities(entity, entitiesToRemove)
        entity."$editFlow.fieldName" = getEntities(editFlow.initialIds + entitiesToSave - entitiesToRemove)
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

    private FieldSelectionStrategy getFieldSelectionStrategy() {
        context.getBean(fieldSelectionStrategyName, FieldSelectionStrategy.class)
    }

    protected List<?> getEntities(List<Long> ids) {
        entityManager.createQuery("SELECT e FROM $selectableFieldHTableName e WHERE e.id IN :idsList",
                selectableFieldEntityClass)
                .setParameter('idsList', ids)
                .resultList
    }
}
