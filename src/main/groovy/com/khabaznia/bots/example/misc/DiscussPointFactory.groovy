package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.example.model.DiscussPoint
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.BotSession.currentUser

@Component('discussPointFactory')
class DiscussPointFactory extends EntityFactory<DiscussPoint> {

    @Override
    DiscussPoint createEntity() {
        new DiscussPoint(userCode: currentUser.code)
    }

    @Override
    String getView(DiscussPoint entity) {
        entity.title
                .concat("\n\n")
                .concat(getAllFieldsView(entity, ['description', 'priority'], true, '\n\n'))
    }
}
