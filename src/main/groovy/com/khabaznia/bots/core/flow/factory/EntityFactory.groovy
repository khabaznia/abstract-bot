package com.khabaznia.bots.core.flow.factory

import com.khabaznia.bots.core.flow.service.FieldViewService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*

/**
 * Abstraction to override default edit flow login
 * @param <T>
 */
@Slf4j
abstract class EntityFactory<T> {

    @Autowired
    protected FieldViewService fieldViewService

    /**
     * Override it to specify additional logic when new entity of {@link T} class is created in edit flow
     *
     * @return should return new filled entity
     */
    T createEntity() {
        Class entityClass = getClass(currentEditFlow)
        log.debug 'Default creation of new entity for class {}', entityClass.simpleName
        entityClass.getDeclaredConstructor().newInstance() as T
    }

    /**
     * Override it to generate custom view for {@link T} class
     * Note! localization should be performed in this method. Use {@link EntityFactory#getAllFieldsView} for process fields
     *
     * @param entity to generate view for
     * @return view as String.
     */
    String getView(T entity) {
        log.debug 'Default view will be printed for entity {}', entity.id.toString()
        fieldViewService.getEntityViewHeader(entity.class, entity.id)
                .concat('\n\n')
                .concat(getAllFieldsView(entity))
    }

    protected String getAllFieldsView(Object entity,
                                      List<String> fieldNames = getViewFields(entity.class),
                                      boolean ignoreEmpty = false, String lineSeparator = System.lineSeparator()) {
        fieldNames.collect {
            fieldViewService.getFieldStringView(entity, it, ignoreEmpty)
        }.findAll().join(lineSeparator)
    }
}
