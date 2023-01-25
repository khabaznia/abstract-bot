package com.khabaznia.bots.core.flow.strategy

import com.khabaznia.bots.core.flow.model.EditFlow
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

/**
 * Strategy intended to specify custom logic for collection fields.
 * @param <T>
 */
abstract class FieldSelectionStrategy<T, P> {

    @Autowired
    protected EntityManager entityManager

    /**
     * Set's current value to edit flow
     *
     * @param editFlow
     */
    void setInitialValues(EditFlow editFlow, Object currentValue){
        editFlow.initialIds = currentValue*.id
        editFlow.selectedIds = currentValue*.id
    }

    /**
     * Returns collection of entities that should be shown as buttons in selection menu
     *
     * @return collection of {@link T} entities
     */
    List<T> getEntitiesToShow(P targetEntity) {
        def query = "SELECT e FROM $selectableFieldHTableName e"
        entityManager.createQuery(query, selectableFieldEntityClass).resultList as List<T>
    }

    /**
     * Updates references with setting it to target entity in entities that were selected in menu
     *
     * @param id of targetEntity
     * @param ids of selectedEntities
     * @return collection of {@link T} selected entities
     */
    List<T> updateSelectedEntities(Object targetEntity, List<Long> selectedEntities) {
        updateReferencesInChildEntities(selectedEntities.unique(), targetEntity, false) as List<T>
    }

    /**
     * Updates references with reseting it from target entity in entities that were unselected in menu
     *
     * @param id of targetEntity
     * @param ids removedEntities
     * @return collection of {@link T} unselected entities
     */
    List<T> updateRemovedEntities(Object targetEntity, List<Long> removedEntities) {
        updateReferencesInChildEntities(removedEntities.unique(), targetEntity, true) as List<T>
    }

    /**
     * Updates target entity with final entities list
     *
     * @param targetEntity
     * @param finalEntitiesList
     * @return collection of {@link T} final entities
     */
    List<T> updateTargetEntity(Object targetEntity, List<Long> finalEntitiesList){
        def fieldName = getCurrentEditFlow().fieldName
        def entities = findAllEntitiesForIds(finalEntitiesList)
        targetEntity."${fieldName}" = entities
        entities as List<T>
    }

    protected List<?> updateReferencesInChildEntities(List<Long> ids, Object entity, boolean isRemove) {
        def entitiesToUpdate = findAllEntitiesForIds(ids)
        entitiesToUpdate?.each {
            def fieldName = currentFieldAnnotation.mappedBy()
            selectableFieldHIsManyToManyRelation
                    ? (isRemove ? (it."$fieldName"?.remove(it)) : (it."$fieldName"?.add(entity)))
                    : (isRemove ? (it."$fieldName" = null) : (it."$fieldName" = entity))
        }
        entitiesToUpdate
    }

    protected List<?> findAllEntitiesForIds(List<Long> ids) {
        entityManager
                .createQuery("SELECT e FROM $selectableFieldHTableName e WHERE e.id IN :idsList", selectableFieldEntityClass)
                .setParameter('idsList', ids)
                .resultList
    }
}
