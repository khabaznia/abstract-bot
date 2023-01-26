package com.khabaznia.bots.core.flow.strategy.impl.processing

import com.khabaznia.bots.core.flow.enums.FieldType
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
    void prepare(EditFlow editFlow) {
        editFlow.type = FieldType.SELECTIVE
        getFieldSelectionStrategy(editFlow).setInitialValues(editFlow, getPersistedValue(editFlow))
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
        strategy.updateSelectedEntities(entity, entitiesToSave)
        strategy.updateRemovedEntities(entity, entitiesToRemove)
        strategy.updateTargetEntity(entity, editFlow.initialIds + entitiesToSave - entitiesToRemove)
    }

    @Override
    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        messages.deleteSelectEntitiesFieldMenu()
        messages.selectedEntitiesSavedMessage()
    }

    private FieldSelectionStrategy getFieldSelectionStrategy(EditFlow editFlow = currentEditFlow) {
        context.getBean(getFieldSelectionStrategyName(editFlow), FieldSelectionStrategy.class)
    }
}
