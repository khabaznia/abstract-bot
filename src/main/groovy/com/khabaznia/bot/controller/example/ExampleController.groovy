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

import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bot.meta.Emoji.LEFT_ARROW
import static com.khabaznia.bot.meta.Emoji.MEDITATE
import static com.khabaznia.bot.meta.Emoji.WARNING_TRIANGLE

@Slf4j
@Component
@BotController()
class ExampleController extends AbstractBotController {

    @Autowired
    private StubService stubService

    @Localized
    @BotRequest(path = EXAMPLE, enableDuplicateRequests = true)
    getReply() {
        sendMessage
                .key('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDITING_MESSAGES, INTEGRATION_TESTS_KEYBOARD],
                                [EXAMPLE.addEmoji(MEDITATE)], [TO_MAIN.addEmoji(LEFT_ARROW)]])
        sendMessage.key(TEST_LOGS + ' - ' + 'sends log message to LOGGING_CHAT'.italic()).delete()
        sendMessage.key('/anyString - ' + 'pins next message'.italic()).delete()
        sendMessage.key('/checkTexts').delete()
    }

    @BotRequest(path = '/checkTexts')
    checkTexts() {
        sendMessage.key('some bold'.bold())
        sendMessage.key('some italic'.italic())
        sendMessage.key('some underline'.underline())
        sendMessage.key('some strikethrough'.strikethrough())

        sendMessage.key('test.bold'.bold())
        sendMessage.key('test.italic'.italic())
        sendMessage.key('test.underline'.underline())
        sendMessage.key('test.strikethrough'.strikethrough())
    }

    @BotRequest(path = TEST_LOGS, after = EXAMPLE)
    getAfterExampleNext() {
        sendLog 'Example message log message.'.strikethrough()
        sendMessage.key('Only after localized example'.underline()).delete()
        sendMessage.key(TEST_LOGS + ' - ' + 'sends warn logs'.italic()).delete()
    }

    @BotRequest(path = TEST_LOGS, after = TEST_LOGS)
    getAfterNextNext() {
        sendWarnLog('Warn log message'.strikethrough())
        sendMessage.key('Works only after /test_logs'.underline()).delete()
        sendMessage.key('/anyString - ' + 'should send log message only for admin'.italic()).delete()
    }

    @BotRequest(after = TEST_LOGS)
    getAfterNextEmptyString() {
        sendLogToAdmin 'Admin log message'.strikethrough()
        sendMessage.key('Any string after /test_logs').delete()
    }

    @BotRequest(after = EXAMPLE)
    getAfterExampleEmptyString() {
        sendMessage.key('Any string after localized example'.underline()).delete()
        sendMessage.key('This message should be pinned').type(MessageType.PINNED)
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.key('modifiable.inline.keyboard')
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
    actionTwo() {
        sendMessage.key('message.action.edit')
                .label('keyboardMessage')
                .inlineKeyboard([['button.example.message': "/exampleMessage"],['button.edit.example.message': "/editExampleMessage"],
                                 [('button.example.back'): BACK_ACTION]])
    }

    @BotRequest(path = '/exampleMessage')
    exampleMessage() {
        sendMessage.key('<b>Some example message</b> - this part will be edited')
                .label('messageToEdit')
    }

    @BotRequest(path = '/editExampleMessage')
    editExampleMessage() {
        editMessage.key("<b>Some example message</b> $WARNING_TRIANGLE $WARNING_TRIANGLE $WARNING_TRIANGLE")
                .label('messageToEdit')
                .delete()
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.key('<b>query</b> - ok').delete()
    }

    @BotRequest(path = '/queryWithParam')
    query(String someUniqueId) {
        sendMessage.key("This param was in button request -> $someUniqueId").delete()
    }

    @Localized
    @BotRequest(path = INTEGRATION_TESTS_KEYBOARD)
    getInline() {
        sendMessage
                .key('Get random stub api?')
                .keyboard(inlineKeyboard.button('Yes', YES_ACTION)
                        .button("No, just count", NO_ACTION, [category: 'science'])
                        .row()
                        .button('button.example.back', LEFT_ARROW, BACK_ACTION)
                )
                .type(MessageType.ONE_TIME_INLINE_KEYBOARD)
    }

    @BotRequest(path = YES_ACTION)
    yesAction() {
        sendMessage.key('Random result from integration ->')
        sendMessage.key(stubService.random().toString())
    }

    @BotRequest(path = NO_ACTION)
    noAction(String category) {
        sendMessage.key('Count of entries for category ' + '\"$category\"'.underline()).binding([category: category])
        def count = stubService.entries(category)
        sendMessage.key('Key count -> $count').binding([count: count as String])
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.key('back').delete()
        EXAMPLE
    }
}
