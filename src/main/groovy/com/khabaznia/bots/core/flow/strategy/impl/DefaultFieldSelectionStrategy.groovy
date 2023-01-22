package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import org.springframework.stereotype.Component


@Component('defaultFieldSelectionStrategy')
class DefaultFieldSelectionStrategy extends FieldSelectionStrategy<Object> {

    @Override
    List<Object> getEntitiesToShow() {
        super.getEntitiesToShow()
    }

    @Override
    List<Object> selectEntities(Object parentEntity, List<Long> selectedEntities) {
        super.selectEntities(parentEntity, selectedEntities)
    }

    @Override
    List<Object> removeEntities(Object parentEntity, List<Long> removedEntities) {
        super.removeEntities(parentEntity, removedEntities)
    }
}
