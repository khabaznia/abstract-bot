package com.khabaznia.bots.core.flow.factory

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getClass
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getCurrentEditFlow

@Slf4j
@Component('defaultEntityFactory')
class DefaultEntityFactory implements EntityFactory {

    @Override
    Object createEntity() {
        Class entityClass = getClass(currentEditFlow)
        log.debug 'Default creation of new entity for class {}', entityClass.simpleName
        return entityClass.getDeclaredConstructor().newInstance()
    }
}
