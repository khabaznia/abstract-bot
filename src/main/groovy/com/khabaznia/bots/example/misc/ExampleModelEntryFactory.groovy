package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.example.model.ExampleModelEntry
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.SessionUtil.currentUser

@Component('exampleModelEntryFactory')
class ExampleModelEntryFactory implements EntityFactory<ExampleModelEntry> {

    @Override
    ExampleModelEntry createEntity() {
        new ExampleModelEntry(userCode: currentUser.code)
    }
}
