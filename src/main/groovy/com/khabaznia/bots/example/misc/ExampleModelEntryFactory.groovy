package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.example.model.ExampleModelEntry
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.SessionUtil.currentUser

@Component('exampleModelEntryFactory')
class ExampleModelEntryFactory extends EntityFactory<ExampleModelEntry> {

    @Override
    ExampleModelEntry createEntity() {
        new ExampleModelEntry(userCode: currentUser.code)
    }

    @Override
    String getView(ExampleModelEntry entity) {
        getAllFieldsView(entity, ['abbreviation', 'someMedia'])
    }
}
