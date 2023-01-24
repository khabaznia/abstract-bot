package com.khabaznia.bots.example.controller

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.flow.dto.EditEntitiesFlowKeyboardDto
import com.khabaznia.bots.core.flow.service.EditFlowKeyboardService
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Localized
import com.khabaznia.bots.core.service.DeepLinkingPathService
import com.khabaznia.bots.core.service.JobService
import com.khabaznia.bots.example.job.ExampleJob
import com.khabaznia.bots.example.model.ExampleModel
import com.khabaznia.bots.example.model.ExampleModelEntry
import com.khabaznia.bots.example.service.ExampleModelService
import com.khabaznia.bots.example.stub.StubService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDateTime

import static com.khabaznia.bots.core.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bots.core.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bots.core.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bots.core.meta.Emoji.*
import static com.khabaznia.bots.core.routing.Constants.COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS
import static com.khabaznia.bots.core.util.SessionUtil.currentChat
import static com.khabaznia.bots.example.Constants.*

@Slf4j
@Component
@BotController()
class ExampleController extends AbstractBotController {

    @Autowired
    private StubService stubService
    @Autowired
    private JobService jobService
    @Autowired
    private DeepLinkingPathService deepLinkingPathService
    @Autowired
    private ExampleModelService exampleModelService
    @Autowired
    private EditFlowKeyboardService editFlowKeyboardService

    @Localized
    @BotRequest(path = EXAMPLE, enableDuplicateRequests = true)
    getReply() {
        sendMessage
                .text('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDIT_FLOW, INTEGRATION_TESTS_KEYBOARD],
                                [JOB_TEST, SEND_MEDIA],
                                [TEST_COMMANDS, EDITING_MESSAGES], [TO_MAIN.addEmoji(LEFT_ARROW)]])
    }

    @Localized
    @BotRequest(path = TEST_COMMANDS)
    testCommands() {
        sendMessage.text(NEXT + ' - ' + 'sends log message to LOGGING_CHAT'.italic()).delete()
        sendMessage.text('/anyString or write anything to chat - ' + 'pins next message'.italic()).delete()
        sendMessage.text('/checkTexts').delete()
        sendMessage.text('/testRetryer').delete()
    }

    @BotRequest(path = '/checkTexts')
    checkTexts() {
        sendMessage.text('some bold'.bold())
        sendMessage.text('some italic'.italic())
        sendMessage.text('some underline'.underline())
        sendMessage.text('some strikethrough'.strikethrough())

        sendMessage.text('test.bold'.bold())
        sendMessage.text('test.italic'.italic())
        sendMessage.text('test.underline'.underline())
        sendMessage.text('test.strikethrough'.strikethrough())
        sendMessage.text('go to ' + 'github repo'.linkUrl('https://github.com/khabaznia/abstract-bot'))
        sendMessage.text('user'.linkUrl(currentChat.code.userMentionUrl()))
    }

    @BotRequest(path = '/testRetryer')
    testRetryer() {
        sendMessage.text('Should try to send message $times times').binding([times: getConfig(COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS)])
        sendMessage.text('Invalid chat id').chatId('33')
    }

    @BotRequest(path = NEXT, after = TEST_COMMANDS)
    getAfterExampleNext() {
        sendLog 'Example message log message.'.strikethrough()
        sendMessage.text('Only after localized example'.underline()).delete()
        sendMessage.text(AFTER_NEXT + ' - ' + 'sends warn logs'.italic()).delete()
    }

    @BotRequest(path = AFTER_NEXT, after = NEXT)
    getAfterNextNext() {
        sendWarnLog('Warn log message'.strikethrough())
        sendMessage.text('Works only after /test_logs'.underline()).delete()
        sendMessage.text('/anyString or write anything to chat - ' + 'should send log message only for admin'.italic()).delete()
    }

    @BotRequest(after = AFTER_NEXT)
    getAfterNextEmptyString() {
        sendLogToAdmin 'Admin log message'.strikethrough()
        sendMessage.text('Any string after /test_logs').delete()
    }

    @BotRequest(after = TEST_COMMANDS)
    getAfterExampleEmptyString() {
        sendMessage.text('Any string after localized example'.underline()).delete()
        sendMessage.text('This message should be pinned').feature(MessageFeature.PINNED)
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.text('modifiable.inline.keyboard')
                .keyboard(inlineKeyboard
                        .button('button.example.simple', AVOCADO, '/query', [(UNLIMITED_CALL): 'true'])
                        .buttonWithBinding('button.example.binding', '/query', [binding: 'Some'])
                        .row()
                        .addButton(inlineButton
                                .text('Deep link button')
                                .url(deepLinkingPathService.generateDeepLinkPath(DEEP_LINK_PATH, [param: currentChat.code])))
                        .row()
                        .oneTimeButton('button.one.time.simple', DEFAULT)
                        .oneTimeButton('button.one.time.with.query', BOAT, '/query')
                        .oneTimeButton('button.one.time.with.param', '/queryWithParam', [someUniqueId: 'data from button'])
                        .row()
                        .switchButton('button.example.switch', DEFAULT, true, [someUniqueId: 'some data from button'])
                        .row()
                        .button('button.with.confirmation', confirmationFlowDto
                                .acceptPath('/confirmationYes')
                                .acceptPathMessage('button.custom.yes')
                                .declinePath('/confirmationNo')
                                .declinePathMessage('button.custom.no')
                                .backPathMessage('button.custom.back')
                                .backPath(TO_MAIN)
                                .redirectParams([reason: 'some reason'])))
    }

    @Localized
    @BotRequest(path = EDITING_MESSAGES)
    editMessageKeyboard() {
        sendMessage.text('message.action.edit')
                .label("${currentChat.code}_keyboardMessage")
                .inlineKeyboard([['button.example.message': "/exampleMessage"]])
    }

    @BotRequest(path = '/exampleMessage')
    sendExampleMessageToEdit() {
        editMessage.text('').emoji(FINGER_DOWN)
                .label("${currentChat.code}_keyboardMessage")
                .inlineKeyboard([['button.edit.example.message': "/editExampleMessage"]])
        sendMessage.text('<b>Some example message</b> - this part will be edited')
                .label("${currentChat.code}_messageToEdit")
    }

    @BotRequest(path = '/editExampleMessage')
    editExampleMessage() {
        editMessage.label("${currentChat.code}_keyboardMessage")
                .keyboard([:])
        editMessage.text("<b>Some example message</b> $WARNING_TRIANGLE $WARNING_TRIANGLE $WARNING_TRIANGLE")
                .label("${currentChat.code}_messageToEdit")
                .delete()
    }

    @BotRequest(path = '/confirmationYes')
    confirmationYes(String reason) {
        sendMessage.text "Confirmed: $reason"
    }

    @BotRequest(path = '/confirmationNo')
    confirmationNo(String reason) {
        sendMessage.text "Declined: $reason"
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.text('<b>query</b> - ok').delete()
    }

    @BotRequest(path = '/queryWithParam')
    query(String someUniqueId) {
        sendMessage.text("This param was in button request -> $someUniqueId").delete()
    }

    @Localized
    @BotRequest(path = INTEGRATION_TESTS_KEYBOARD)
    getInline() {
        sendMessage
                .text('Get random stub api?')
                .keyboard(inlineKeyboard.button('Yes', YES_ACTION)
                        .button("No, just count", NO_ACTION, [category: 'science'])
                        .row()
                        .button('button.example.back', LEFT_ARROW, BACK_ACTION))
                .feature(MessageFeature.ONE_TIME_INLINE_KEYBOARD)
    }

    @Localized
    @BotRequest(path = JOB_TEST)
    sendJob() {
        sendMessage.text 'Job will send you message in 5 seconds'

        ExampleJob job = context.getBean('exampleJob')
        job.chatToSend = currentChat.code
        jobService.scheduleJob(job, LocalDateTime.now().plusSeconds(5).toDate())
    }

    @Localized
    @BotRequest(path = EDIT_FLOW)
    editFlowExample() {
        def model = exampleModelService.getAll().find()
        def keyboard = inlineKeyboard
        if (model)
            keyboard
                    .button(null, CHECK, editFieldFlowDto
                            .fieldName('field1')
                            .entityToEdit(model)
                            .successPath(EXAMPLE))
                    .button('Super-flag', editFieldFlowDto
                            .entityToEdit(model)
                            .fieldName('flag')
                            .successText('Yeap! updated')
                            .successPath('/queryWithParam')
                            .redirectParams([someUniqueId: 'someUniqueId']))
                    .button('localized', FEEDBACK, editFieldFlowDto
                            .entityId(model.id)
                            .fieldName('name')
                            .entityClass(model.class)
                            .successPath(EDIT_FLOW))
                    .row()
                    .button('Edit enitty ', editEntityFlowDto
                            .entityToEdit(model)
                            .fieldsInRow(1)
                            .enterTextBinding([entityName: 'someName'])
                            .enterText('you are going to edit $entityName')
                            .successPath('/queryWithParam')
                            .backPath(EDIT_FLOW)
                            .redirectParams([someUniqueId: 'someUniqueId']))
                    .row()
        keyboard.button('Edit all ExampleModels', '/editExampleModels').row()
        keyboard.button('Edit all ExampleModelEntries', '/editExampleModelEntries')
        sendMessage.text('Add new or edit existing')
                .keyboard(keyboard)
    }

    @BotRequest(path = '/editExampleModels')
    editExampleModels(String myCustomParam, String entityId) {
        if (myCustomParam) sendMessage.text myCustomParam
        if (entityId) sendMessage.text "Entity was updated/created -> $entityId"
        sendMessage.text('Choose action')
                .keyboard(editFlowKeyboardService.addButtons(inlineKeyboard,
                        new EditEntitiesFlowKeyboardDto<ExampleModel>()
                                .entityNameRetriever({ ExampleModel it -> it.field1 })
                                .entityClass(ExampleModel.class)
                                .entities(exampleModelService.getAll())
                                .thisStepPath('/editExampleModels')
                                .backPath(EDIT_FLOW)
                                .createNewEntitySuccessMessage('YEEEEEES, CREATED!')
                                .deleteEntitySuccessMessage("DELETED")
                                .redirectParams([myCustomParam: 'Some custom param'])
                                .canCreateNewEntity(true)
                                .canDeleteEntities(true)
                                .entityFactory('exampleModelFactory'))
                )
    }

    @BotRequest(path = '/editExampleModelEntries')
    editExampleModelEntries() {
        def keyboard = inlineKeyboard
        keyboard.button('Sample button', DEFAULT)
                .row()
        sendMessage.text('Choose action')
                .keyboard(editFlowKeyboardService.addButtons(keyboard,
                        new EditEntitiesFlowKeyboardDto<ExampleModelEntry>()
                                .entityNameRetriever({ it.abbreviation })
                                .entityClass(ExampleModelEntry.class)
                                .entities(exampleModelService.getAllEntries())
                                .thisStepPath('/editExampleModelEntries')
                                .fieldsInRow(2)
                                .entitiesInRow(5)
                                .backPath(EDIT_FLOW)
                                .canDeleteEntities(false))
                        .row()
                        .button('Another sample button', DEFAULT)
                )
    }

    @Localized
    @BotRequest(path = SEND_MEDIA)
    sendMediaExample() {
        sendPhoto
                .text('some caption')
                .fileIdentifier('logo.jpg')
    }

    @BotRequest(path = YES_ACTION)
    yesAction() {
        sendMessage.text('Random result from integration ->')
        sendMessage.text(stubService.random().toString())
    }

    @BotRequest(path = NO_ACTION)
    noAction(String category) {
        sendMessage.text('Count of entries for category ' + '\"$category\"'.underline()).binding([category: category])
        def count = stubService.entries(category)
        sendMessage.text('Key count -> $count').binding([count: count as String])
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.text('back').delete()
        EXAMPLE
    }

    @BotRequest(path = DEEP_LINK_PATH)
    deepLinkExample(String param) {
        sendMessage.text("Hello. You have joined from chat - $param")
    }
}
