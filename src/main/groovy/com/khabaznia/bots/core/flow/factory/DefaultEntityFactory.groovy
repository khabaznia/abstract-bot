package com.khabaznia.bots.core.flow.factory

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getClass
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getCurrentEditFlow

@Slf4j
@Component('defaultEntityFactory')
class DefaultEntityFactory extends EntityFactory<Object> {

    @Override
    Object createEntity() {
        super.createEntity()
    }

    @Override
    String getView(Object entity) {
        fieldViewService.getEntityViewHeader(entity.class, entity.id)
                .concat('\n\n')
                .concat(getAllFieldsView(entity))
    }
}
