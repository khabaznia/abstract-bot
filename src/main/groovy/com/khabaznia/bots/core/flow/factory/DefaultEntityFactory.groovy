package com.khabaznia.bots.core.flow.factory

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component('defaultEntityFactory')
class DefaultEntityFactory extends EntityFactory<Object> {

    @Override
    Object createEntity() {
        super.createEntity()
    }

    @Override
    String getView(Object entity) {
        log.debug 'Default view will be printed for entity {}', entity.id.toString()
        fieldViewService.getEntityViewHeader(entity.class, entity.id)
                .concat('\n\n')
                .concat(getAllFieldsView(entity))
    }
}
