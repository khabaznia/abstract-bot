package com.khabaznia.bots.example.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.flow.dto.EditEntitiesFlowKeyboardDto
import com.khabaznia.bots.core.flow.service.EditFlowKeyboardService
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import com.khabaznia.bots.example.model.DiscussPoint
import com.khabaznia.bots.example.model.Meeting
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager

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

    @Localized
    @BotRequest(path = EDIT_FLOW)
    editFlowExample() {
        def keyboard = inlineKeyboard
        keyboard.button('Check my meetings', MY_MEETINGS).row()
        keyboard.button('Edit discuss points', EDIT_MY_DISCUSS_POINTS).row()
        sendMessage.text('Example models to edit/create: <b>meeting</b> and <b>discuss points</b>.\n\nChoose model 👇')
                .keyboard(keyboard)
    }

    @BotRequest(path = MY_MEETINGS)
    myMeetingsMenu() {
        def myMeetings = entityManager
                .createQuery("SELECT m FROM meeting m WHERE m.userCode = :userCode")
                .setParameter('userCode', currentUser.code)
                .resultList
        sendMessage.text('Here is the list of meetings that you are owner of')
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
        sendMessage.text('Choose discuss point to edit')
                .keyboard(editFlowKeyboardService.addButtons(inlineKeyboard,
                        new EditEntitiesFlowKeyboardDto<DiscussPoint>()
                                .entityNameRetriever({ it -> it.title })
                                .entityClass(DiscussPoint.class)
                                .entities(discussPoints)
                                .fieldsInRow(1)
                                .entitiesInRow(2)
                                .canDeleteEntities(false)
                                .canCreateNewEntity(false)
                        .entityFactory('discussPointFactory')
                                .thisStepPath(EDIT_MY_DISCUSS_POINTS)
                                .backPath(EDIT_FLOW)))
    }
}
