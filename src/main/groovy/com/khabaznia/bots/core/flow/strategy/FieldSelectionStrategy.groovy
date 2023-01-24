package com.khabaznia.bots.core.flow.strategy

import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

/**
 * Strategy intended to specify custom logic for collection fields.
 * @param <T>
 */
abstract class FieldSelectionStrategy<T> {

    @Autowired
    protected EntityManager entityManager

    /**
     * Returns collection of entities that should be shown as buttons in selection menu
     *
     * @return collection of {@link T} entities
     */
    List<T> getEntitiesToShow() {
        def query = "SELECT e FROM $selectableFieldHTableName e"
        entityManager.createQuery(query, selectableFieldEntityClass).resultList as List<T>
    }

    /**
     * Updates references with setting it to parent entity in entities that were selected in menu
     *
     * @param id of parentEntity
     * @param ids of selectedEntities
     * @return collection of {@link T} selected entities
     */
    List<T> selectEntities(Object parentEntity, List<Long> selectedEntities) {
        updateReferencesInChildEntities(selectedEntities.unique(), parentEntity, false) as List<T>
    }

    /**
     * Updates references with reseting it from parent entity in entities that were unselected in menu
     *
     * @param id of parentEntity
     * @param ids removedEntities
     * @return collection of {@link T} unselected entities
     */
    List<T> removeEntities(Object parentEntity, List<Long> removedEntities) {
        updateReferencesInChildEntities(removedEntities.unique(), parentEntity, true) as List<T>
    }

    protected List<?> updateReferencesInChildEntities(List<Long> ids, Object entity, boolean isRemove) {
        def entitiesToUpdate = getSelectedEntities(ids)
        entitiesToUpdate?.each {
            def fieldName = currentFieldAnnotation.mappedBy()
            selectableFieldHIsManyToManyRelation
                    ? (isRemove ? (it."$fieldName"?.remove(it)) : (it."$fieldName"?.add(entity)))
                    : (isRemove ? (it."$fieldName" = null) : (it."$fieldName" = entity))
        }
        entitiesToUpdate
    }

    protected List<?> getSelectedEntities(List<Long> ids) {
        entityManager
                .createQuery("SELECT e FROM $selectableFieldHTableName e WHERE e.id IN :idsList", selectableFieldEntityClass)
                .setParameter('idsList', ids)
                .resultList
    }
}
