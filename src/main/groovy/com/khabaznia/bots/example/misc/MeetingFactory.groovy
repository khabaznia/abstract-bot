package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.factory.EntityFactory
import com.khabaznia.bots.example.model.Meeting
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.BotSession.currentUser

@Component('meetingFactory')
class MeetingFactory extends EntityFactory<Meeting> {

    @Override
    Meeting createEntity() {
        new Meeting(
                userCode: currentUser.code,
                upcoming: true)
    }
}
