package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import org.springframework.stereotype.Component


@Component('defaultFieldSelectionStrategy')
class DefaultFieldSelectionStrategy extends FieldSelectionStrategy<Object, Object> {

    @Override
    List<Object> getEntitiesToShow(Object targetEntity) {
        super.getEntitiesToShow(targetEntity)
    }

    @Override
    List<Object> updateSelectedEntities(Object targetEntity, List<Long> selectedEntities) {
        super.updateSelectedEntities(targetEntity, selectedEntities)
    }

    @Override
    List<Object> updateRemovedEntities(Object targetEntity, List<Long> removedEntities) {
        super.updateRemovedEntities(targetEntity, removedEntities)
    }
}
