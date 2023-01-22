package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.example.model.ExampleModel
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.SessionUtil.currentUser

@Component('exampleModelFactory')
class ExampleModelFactory implements EntityFactory<ExampleModel> {

    @Override
    ExampleModel createEntity() {
        new ExampleModel(
                serviceFlag: true,
                field2: 'some_value',
                userCode: currentUser.code)
    }
}
