package com.khabaznia.bots.example.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.dto.EditEntitiesFlowKeyboardDto
import com.khabaznia.bots.core.flow.service.EditFlowKeyboardService
import com.khabaznia.bots.core.meta.Emoji
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import com.khabaznia.bots.example.model.DiscussPoint
import com.khabaznia.bots.example.model.Meeting
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager

import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.util.SessionUtil.currentUser
import static com.khabaznia.bots.example.Constants.*

@Slf4j
@Component
@BotController
class EditFlowExampleController extends AbstractBotController {

    @Autowired
    private EditFlowKeyboardService editFlowKeyboardService
    @Autowired
    private EntityManager entityManager


    @BotRequest(path = '/editFieldFlow')
    editFieldFlow() {
        def pointToDiscuss = entityManager
                .createQuery("SELECT m FROM discuss_point m WHERE m.userCode = :userCode")
                .setParameter('userCode', currentUser.code)
                .resultList.find() as DiscussPoint
        if (pointToDiscuss)
            sendMessage.text('Press button to edit localized description  👇🏽')
                    .keyboard(inlineKeyboard
                            .button('Edit description', editFieldFlowDto
                                    .entityToEdit(pointToDiscuss)
                                    .fieldName('description')
                                    .enterText('Update current description of $title')
                                    .enterTextBinding(['title': pointToDiscuss.title])
                                    .successPath(TO_MAIN)
                            ))
    }

    @BotRequest(path = '/editEntityFlow')
    editEntityFlow() {
        def meetingId = entityManager
                .createQuery("SELECT m FROM meeting m WHERE m.userCode = :userCode")
                .setParameter('userCode', currentUser.code)
                .resultList?.find()?.id as Long
        if (meetingId)
            sendMessage.text('Press button to edit entity 👇🏽')
                    .keyboard(inlineKeyboard
                            .button('Edit my awesome meeting', editEntityFlowDto
                                    .entityId(meetingId)
                                    .entityClass(Meeting.class)
                                    .entityFactory('meetingFactory')
                                    .enterText('Edit entity with id $id')
                                    .enterTextBinding(['id': meetingId.toString()])
                                    .backPath(EDIT_FLOW)
                            ))
    }

    @Localized
    @BotRequest(path = EDIT_FLOW)
    editFlowExample() {
        sendMessage.text('text.select.meetings')
                .keyboard(inlineKeyboard
                        .button('button.my.meetings', Emoji.TEAM, MY_MEETINGS).row()
                        .button('button.my.points.to.discuss', Emoji.EDIT, EDIT_MY_DISCUSS_POINTS).row())
    }

    @BotRequest(path = MY_MEETINGS)
    myMeetingsMenu() {
        def myMeetings = entityManager
                .createQuery("SELECT m FROM meeting m WHERE m.userCode = :userCode")
                .setParameter('userCode', currentUser.code)
                .resultList
        sendMessage.text('text.meetings.select.entity')
                .keyboard(editFlowKeyboardService.addButtons(inlineKeyboard,
                        new EditEntitiesFlowKeyboardDto<Meeting>()
                                .entityClass(Meeting.class)
                                .entities(myMeetings)
                                .thisStepPath(MY_MEETINGS)
                                .entityFactory('meetingFactory')
                                .backPath(EDIT_FLOW)))
    }

    @BotRequest(path = EDIT_MY_DISCUSS_POINTS)
    editExampleModelEntries() {
        def discussPoints = entityManager
                .createQuery("SELECT m FROM discuss_point m WHERE m.userCode = :userCode")
                .setParameter('userCode', currentUser.code)
                .resultList
        sendMessage.text('text.points.to.discuss.select')
                .keyboard(editFlowKeyboardService.addButtons(inlineKeyboard,
                        new EditEntitiesFlowKeyboardDto<DiscussPoint>()
                                .entityNameRetriever({ it -> it.title })
                                .entityClass(DiscussPoint.class)
                                .entities(discussPoints)
                                .fieldsInRow(1)
                                .entitiesInRow(2)
                                .canDeleteEntities(false)
                                .canCreateNewEntity(false)
                                .thisStepPath(EDIT_MY_DISCUSS_POINTS)
                                .backPath(EDIT_FLOW)))
    }
}
