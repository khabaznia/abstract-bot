package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import com.khabaznia.bots.example.model.ExampleModelEntry
import com.khabaznia.bots.example.service.ExampleModelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exampleModelEntryUserSelectionStrategy')
class ExampleModelEntryUserSelectionStrategy extends FieldSelectionStrategy<ExampleModelEntry> {

    @Autowired
    private ExampleModelService service

    @Override
    List<ExampleModelEntry> getEntitiesToShow() {
        service.allEntriesForCurrentUser
    }
}
