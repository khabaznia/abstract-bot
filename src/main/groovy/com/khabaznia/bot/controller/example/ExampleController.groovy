package com.khabaznia.bot.controller.example

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.integration.StubService
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*
import static com.khabaznia.bot.meta.Emoji.*

@Slf4j
@Component
@BotController()
class ExampleController extends AbstractBotController {


    @BotRequest(path = '/firstAction') // first action
    someFirstAction() {
        sendMessage.text('Tap to go to next action') // send user simple message with one button
                .keyboard(inlineKeyboard.button("Got to next action", '/nextAction',
                        [category: 'science'])) // parameter in button
    }

    @BotRequest(path = '/nextAction', after = "/firstAction") // this mapping will be invoked ONLY after /firstAction
    // Method returns String and this is trigger to forward to command mapping that will be returned
    String simpleCommandAfterPath(String category) { // The 'category' parameter will be used from button param ('science' in this example)
        sendMessage.text("You-ve got the category -> $category")
        '/afterNext' // forward to another command
    }

    @BotRequest(path = '/afterNext') // This method is regular command, but also will be invoked from previous action
    afterNextAction() {
        sendMessage.text('Tap to go to next action')
                .keyboard(inlineKeyboard.button("Got to next action", '/nextAction', [category: 'science']))
    }





    @Autowired
    private StubService stubService

    @Localized
    @BotRequest(path = EXAMPLE, enableDuplicateRequests = true)
    getReply() {
        sendMessage
                .text('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDITING_MESSAGES, INTEGRATION_TESTS_KEYBOARD],
                                [EXAMPLE.addEmoji(MEDITATE)], [TO_MAIN.addEmoji(LEFT_ARROW)]])
        sendMessage.text(NEXT + ' - ' + 'sends log message to LOGGING_CHAT'.italic()).delete()
        sendMessage.text('/anyString - ' + 'pins next message'.italic()).delete()
        sendMessage.text('/checkTexts').delete()
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
    }

    @BotRequest(path = NEXT, after = EXAMPLE)
    getAfterExampleNext() {
        sendLog 'Example message log message.'.strikethrough()
        sendMessage.text('Only after localized example'.underline()).delete()
        sendMessage.text(AFTER_NEXT + ' - ' + 'sends warn logs'.italic()).delete()
    }

    @BotRequest(path = AFTER_NEXT, after = NEXT)
    getAfterNextNext() {
        sendWarnLog('Warn log message'.strikethrough())
        sendMessage.text('Works only after /test_logs'.underline()).delete()
        sendMessage.text('/anyString - ' + 'should send log message only for admin'.italic()).delete()
    }

    @BotRequest(after = NEXT)
    getAfterNextEmptyString() {
        sendLogToAdmin 'Admin log message'.strikethrough()
        sendMessage.text('Any string after /test_logs').delete()
    }

    @BotRequest(after = EXAMPLE)
    getAfterExampleEmptyString() {
        sendMessage.text('Any string after localized example'.underline()).delete()
        sendMessage.text('This message should be pinned').type(MessageType.PINNED)
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.text('modifiable.inline.keyboard')
                .keyboard(inlineKeyboard
                        .button('button.example.simple', Emoji.AVOCADO, '/query', [(UNLIMITED_CALL): 'true'])
                        .buttonWithBinding('button.example.binding', '/query', [binding: 'Some'])
                        .row()
                        .oneTimeButton('button.one.time.simple', DEFAULT)
                        .oneTimeButton('button.one.time.with.query', Emoji.BOAT, '/query')
                        .oneTimeButton('button.one.time.with.param', '/queryWithParam', [someUniqueId: 'data from button'])
                        .row()
                        .switchButton('button.example.switch', DEFAULT, true, [someUniqueId: 'some data from button']))
    }

    @Localized
    @BotRequest(path = EDITING_MESSAGES)
    editMessageKeyboard() {
        sendMessage.text('message.action.edit')
                .label("${chatId}_keyboardMessage")
                .inlineKeyboard([['button.example.message': "/exampleMessage"]])
    }

    @BotRequest(path = '/exampleMessage')
    sendExampleMessageToEdit() {
        editMessage.text('').emoji(Emoji.FINGER_DOWN)
                .label("${chatId}_keyboardMessage")
                .inlineKeyboard([['button.edit.example.message': "/editExampleMessage"]])
        sendMessage.text('<b>Some example message</b> - this part will be edited')
                .label("${chatId}_messageToEdit")
    }

    @BotRequest(path = '/editExampleMessage')
    editExampleMessage() {
        editMessage.label("${chatId}_keyboardMessage")
                .keyboard([:])
        editMessage.text("<b>Some example message</b> $WARNING_TRIANGLE $WARNING_TRIANGLE $WARNING_TRIANGLE")
                .label("${chatId}_messageToEdit")
                .delete()
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
                        .button('button.example.back', LEFT_ARROW, BACK_ACTION)
                )
                .type(MessageType.ONE_TIME_INLINE_KEYBOARD)
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
}
