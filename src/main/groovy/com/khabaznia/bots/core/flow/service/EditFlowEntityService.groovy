package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Component
@Transactional
class EditFlowEntityService {

    @Autowired
    private EntityManager entityManager
    @Autowired
    private ApplicationContext context

    String getEntityView(EditEntityFlowDto editEntityFlowDto) {
        def factoryBeanName = editEntityFlowDto.entityFactory ?: getEntityFactoryName(editEntityFlowDto.entityClass)
        def factory = context.getBean(factoryBeanName, EntityFactory.class)
        factory.getView(entityManager.find(editEntityFlowDto.entityClass, editEntityFlowDto.entityId))
    }

    Map<Object, Boolean> getEntitiesToSelect() {
        def selectedEntities = context.getBean(fieldSelectionStrategyName, FieldSelectionStrategy.class)
                .entitiesToShow
        def selectedIds = currentEditFlow.selectedIds
        def resultMap = selectedEntities
                .collectEntries { [(it): selectedIds.contains(it.id).toBoolean()] }
        resultMap.putAll((selectedIds - resultMap.keySet()*.id)
                .collectEntries { [(entityManager.find(selectableFieldEntityClass, it)): Boolean.TRUE] })
        resultMap as Map<String, Boolean>
    }

    Object getEntity() {
        def editFlow = currentEditFlow
        def entityClass = getClass(editFlow)
        editFlow.entityId
                ? entityManager.find(entityClass, editFlow.entityId)
                : getNewEntity(editFlow, entityClass)
    }

    private Object getNewEntity(EditFlow editFlow, Class<?> entityClass) {
        def factoryBeanName = editFlow.entityFactory ?: getEntityFactoryName(entityClass)
        context.getBean(factoryBeanName, EntityFactory.class)
                .createEntity()
    }

    void saveEntity(Object entity) {
        entityManager.persist(entity)
        entityManager.flush()
    }

    void deleteEntity(Class entityClass, Long entityId) {
        def entity = entityManager.find(entityClass, entityId)
        entityManager.remove(entity)
        entityManager.flush()
    }
}
