package com.khabaznia.bots.core.flow.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Component
@Transactional
class EditFlowEntityService {

    @Autowired
    private EntityManager entityManager

    Map<Object, Boolean> getEntitiesToSelect() {
        def query = "SELECT e FROM $selectableFieldHTableName e"
        def resultMap = entityManager.createQuery(query, selectableFieldEntityClass).resultList
                .collectEntries { [(it): currentEditFlow.selectedIds.contains(it.id).toBoolean()] }
        resultMap.putAll((currentEditFlow.selectedIds - resultMap.keySet()*.id)
                .collectEntries { [(entityManager.find(selectableFieldEntityClass, it)): Boolean.TRUE] })
        resultMap as Map<String, Boolean>
    }

    Object getEntity() {
        def editFlow = currentEditFlow
        def entityClass = getClass(editFlow)
        editFlow.entityId
                ? entityManager.find(entityClass, editFlow.entityId)
                : entityClass.getDeclaredConstructor().newInstance()
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
