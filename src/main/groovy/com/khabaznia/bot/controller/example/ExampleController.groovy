package com.khabaznia.bot.controller.example

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.integration.StubService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.BUTTON_PARAMETERS.UNLIMITED_CALL
import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*
import static com.khabaznia.bot.core.Constants.COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS
import static com.khabaznia.bot.meta.Emoji.*

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
                .text('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDITING_MESSAGES, INTEGRATION_TESTS_KEYBOARD],
                                [EXAMPLE.addEmoji(TEST_EMOJI_SET), TEST_COMMANDS], [TO_MAIN.addEmoji(LEFT_ARROW)]])
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
        sendMessage.text('This message should be pinned').type(MessageType.PINNED)
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.text('modifiable.inline.keyboard')
                .keyboard(inlineKeyboard
                        .button('button.example.simple', AVOCADO, '/query', [(UNLIMITED_CALL): 'true'])
                        .buttonWithBinding('button.example.binding', '/query', [binding: 'Some'])
                        .row()
                        .oneTimeButton('button.one.time.simple', DEFAULT)
                        .oneTimeButton('button.one.time.with.query', BOAT, '/query')
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
        editMessage.text('').emoji(FINGER_DOWN)
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
