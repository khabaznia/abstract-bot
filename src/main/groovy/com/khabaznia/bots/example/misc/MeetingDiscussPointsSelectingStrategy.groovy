package com.khabaznia.bots.example.misc

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import com.khabaznia.bots.example.model.DiscussPoint
import com.khabaznia.bots.example.model.Meeting
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.util.SessionUtil.getCurrentUser

@Component('meetingSelectionStrategy')
class MeetingDiscussPointsSelectingStrategy extends FieldSelectionStrategy<DiscussPoint, Meeting> {

    @Override
    List<DiscussPoint> getEntitiesToShow(Meeting targetEntity) {
        entityManager
                .createQuery("SELECT m FROM discuss_point m WHERE m.userCode = :userCode AND m.meeting= :meeting")
                .setParameter('userCode', currentUser.code)
                .setParameter('meeting', targetEntity)
                .resultList
    }
}

