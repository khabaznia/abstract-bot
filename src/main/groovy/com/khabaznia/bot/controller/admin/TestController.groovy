package com.khabaznia.bot.controller.admin

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.enums.Role
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
