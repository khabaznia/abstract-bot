package com.khabaznia.bot.controller.example

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.integration.StubService
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.TO_MAIN
import static com.khabaznia.bot.controller.Constants.EXAMPLE_CONTROLLER.*

@Slf4j
@Component
@BotController()
class ExampleController extends AbstractBotController {

    @Autowired
    private StubService stubService

    @Localized
    @BotRequest(path = EXAMPLE)
    getReply() {
        sendMessage
                .key('Here is your reply keyboard')
                .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDITING_MESSAGES, INTEGRATION_TESTS_KEYBOARD], [TO_MAIN]])
        sendMessage.key(NEXT).delete()
        sendMessage.key('/anything').delete()
    }

    @BotRequest(path = NEXT, after = EXAMPLE)
    getAfterExampleNext() {
        sendLog 'Send example message'
        sendMessage.key('Only after localized example').delete()
        sendMessage.key(NEXT).delete()
    }

    @BotRequest(path = NEXT, after = NEXT)
    getAfterNextNext() {
        sendWarnLog('Test warn')
        sendMessage.key('NEXT that only after NEXT').delete()
        sendMessage.key('/anything').delete()
    }

    @BotRequest(after = NEXT)
    getAfterNextEmptyString() {
        sendLogToAdmin 'Edited example message'
        sendMessage.key('Simple string after NEXT').delete()
    }

    @BotRequest(after = EXAMPLE)
    getAfterExampleEmptyString() {
        sendMessage.key('Simple string after EXAMPLE').delete()
    }

    @Localized
    @BotRequest(path = MODIFIABLE_INLINE_KEYBOARD)
    getFeatures() {
        sendMessage.key('action two')
                .keyboard(inlineKeyboard
                        .button('example.simple.button', '/query')
                        .button('example.emoji.button', Emoji.AVOCADO, '/query')
                        .buttonWithBinding('example.binding.button', '/query', [binding: 'some'])
                        .row()
                        .oneTimeButton('OT button', '/query')
                        .oneTimeButton('OT emoji', Emoji.BOAT, '/query')
                        .oneTimeButton('OT param', '/queryWithParam', [someUniqueId: '34'])
                        .row()
                        .switchButton('SB with param', '/queryWithParam', true, [someUniqueId: '34']))
    }

    @Localized
    @BotRequest(path = EDITING_MESSAGES)
    actionTwo() {
        sendMessage.key('Edit message ')
                .inlineKeyboard([['example.button.one': "/exampleMessage", 'example.button.two': "/editExampleMessage"],
                                 [('button.example.back'): BACK_ACTION]])
    }

    @BotRequest(path = '/exampleMessage')
    exampleMessage() {
        sendMessage.key('<b>Some example message</b> - this part will be edited')
                .label('messageToEdit')
    }

    @BotRequest(path = '/editExampleMessage')
    editExampleMessage() {
        editMessage.key('<b>Some example message</b> - !edited!')
                .label('messageToEdit')
                .delete()
    }

    @BotRequest(path = '/query')
    query() {
        sendMessage.key('<b>query</b> - ok').delete()
    }

    @BotRequest(path = '/queryWithParam')
    query(String someUniqueId) {
        sendMessage.key('query - <i>ok</i>').delete()
        sendMessage.key(someUniqueId).delete()
    }

    @Localized
    @BotRequest(path = INTEGRATION_TESTS_KEYBOARD)
    getInline() {
        sendMessage
                .key('Get random stub api?')
                .keyboard(inlineKeyboard.button('Yes', YES_ACTION)
                        .button("No, just count", NO_ACTION, [category: 'science'])
                        .row()
                        .button('button.example.back', BACK_ACTION)
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
        sendMessage.key('Count of entries for category $category').binding([category: category])
        def count = stubService.entries(category)
        sendMessage.key('Key count -> $count').binding([count: count as String])
    }

    @BotRequest(path = BACK_ACTION)
    String backAction() {
        sendMessage.key('back')
        EXAMPLE
    }
}
