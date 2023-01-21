package com.khabaznia.bots.core.flow.strategy

import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager

abstract class FieldResolvingStrategy {

    @Autowired
    protected EntityManager entityManager

    abstract Object getValue(Class entityClass, Long entityId, String fieldName)

    protected Object getPersistedValue(Class entityClass, Long entityId, String fieldName) {
        entityManager.find(entityClass, entityId)?."${fieldName}"
    }
}
