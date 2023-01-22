package com.khabaznia.bots.common.controller.admin

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.enums.Role
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component
@BotController
class TestController extends AbstractBotController {

    private void doTest() {
        //write your test here

    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = '/test')
    testingStuffForAdminOnly() {
        sendMessage.text('Do you want to do THIS?')
                .keyboard(inlineKeyboard.button('Yes, run my awesome test', '/test'))
                .delete()
        doTest()
    }

    @BotRequest(path = '/easterEgg')
    easterEgg() {
        sendMessage.text('Yes! You\'ve found \'an Easter Egg\'.').delete()
    }

    //Easter Egg for users
    private static void logIt(String string) {
        log.debug '\n\n\n\n-------------!!!!!!!!!!!!!!!!!------------------------!!!!!!!!!!!!!!!!!------------------------!!!!!!!!!!!!!!!!!-----------'
        log.debug(string)
        log.debug '\n\n\n\n-------------!!!!!!!!!!!!!!!!!------------------------!!!!!!!!!!!!!!!!!------------------------!!!!!!!!!!!!!!!!!-----------'
    }
}
