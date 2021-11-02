package com.khabaznia.bot.controller.admin

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.security.Role
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.*
import static com.khabaznia.bot.controller.Constants.COMMON.*


@Slf4j
@Component
@BotController(path = ADMIN_CONTROLLER)
class AdminController extends AbstractBotController {

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = START)
    onStart() {

    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = FEATURES_LIST)
    getFeatures() {

    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = SWITCH_FEATURE)
    String switchFeature() {

        FEATURES_LIST
    }
}
