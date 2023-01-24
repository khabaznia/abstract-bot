package com.khabaznia.bots.core.flow.factory

import com.khabaznia.bots.core.flow.service.FieldViewService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

@Slf4j
abstract class EntityFactory<T> {

    @Autowired
    protected FieldViewService fieldViewService

    T createEntity() {
        Class entityClass = getClass(currentEditFlow)
        log.debug 'Default creation of new entity for class {}', entityClass.simpleName
        entityClass.getDeclaredConstructor().newInstance() as T
    }

    String getView(T entity) { null }

    protected String getAllFieldsView(Object entity,
                                      List<String> fieldNames = getViewFields(entity.class),
                                      boolean ignoreEmpty = false) {
        fieldNames.collect {
            fieldViewService.getFieldStringView(entity, it, ignoreEmpty)
        }.findAll().join(System.lineSeparator())
    }
}
