package com.khabaznia.bots.core.flow.strategy

import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

abstract class FieldSelectionStrategy<T> {

    @Autowired
    protected EntityManager entityManager

    List<T> getEntitiesToShow() {
        def query = "SELECT e FROM $selectableFieldHTableName e"
        entityManager.createQuery(query, selectableFieldEntityClass).resultList as List<T>
    }

    List<T> selectEntities(Object parentEntity, List<Long> selectedEntities) {
        updateReferencesInChildEntities(selectedEntities.unique(), parentEntity, false) as List<T>
    }

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
