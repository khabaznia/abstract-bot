package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.core.Constants.*

@Slf4j
@Component
class MessageToCommandMapper {

    @Autowired
    BotControllerContainer controllerContainer
    @Autowired
    UpdateService updateService

    BotControllerProxy getControllerForUpdate(Update update) {
        def requestedPath = getPathForStringUpdate(update)
        controllerContainer.getController(getPreviousPath(), requestedPath)
    }

    BotControllerProxy getControllerForPath(String path) {
        controllerContainer.getController(getPreviousPath(), path)
    }

    private String getPathForStringUpdate(Update update) {
        log.trace 'Getting path for update'
        def controllerPath = updateService.getMessageFromUpdate(update)
        if (controllerPath?.startsWith(COMMANDS_DELIMITER)) {
            controllerPath = controllerPath.split(PARAMETERS_PREFIX)[0]
        }
        controllerPath
    }

    private static String getPreviousPath() {
        SessionUtil.currentChat.lastAction
    }
}
