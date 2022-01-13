package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.service.UpdateService
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

    BotControllerProxy getController(Update update) {
        getController(getPathForStringUpdate(update))
    }

    BotControllerProxy getController(String path) {
        controllerContainer.getController(path)
    }

    private String getPathForStringUpdate(Update update) {
        log.trace 'Getting path for update'
        def controllerPath = updateService.getMessageFromUpdate(update)
        if (controllerPath?.startsWith(COMMANDS_DELIMITER)) {
            controllerPath = controllerPath.split(PARAMETERS_PREFIX)[0]
        }
        controllerPath
    }

}
