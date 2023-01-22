package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import com.khabaznia.bots.example.model.ExampleModelEntry
import com.khabaznia.bots.example.service.ExampleModelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exampleModelEntryAbbreviationSelectionStrategy')
class ExampleModelEntryAbbreviationSelectionStrategy extends FieldSelectionStrategy<ExampleModelEntry> {

    @Autowired
    private ExampleModelService service

    @Override
    List<ExampleModelEntry> getEntitiesToShow() {
        service.allWithNotEmptyAbbreviation
    }

    @Override
    List<ExampleModelEntry> removeEntities(Object parentEntity, List<Long> removedEntities) {
        def entities = super.removeEntities(parentEntity, removedEntities) as List<ExampleModelEntry>
        entities.each { it.abbreviation = null }
    }
}
