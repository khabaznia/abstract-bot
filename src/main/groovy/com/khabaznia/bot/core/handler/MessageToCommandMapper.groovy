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
    private BotControllerContainer controllerContainer
    @Autowired
    private UpdateService updateService

    BotControllerProxy getController(Update update) {
        getController(getPathForStringUpdate(update))
    }

    BotControllerProxy getController(String path) {
        controllerContainer.getController(path)
    }

    private String getPathForStringUpdate(Update update) {
        def controllerPath = updateService.getMessageFromUpdate(update)
        log.info 'Request message -> {}', controllerPath
        if (controllerPath?.startsWith(COMMANDS_DELIMITER)) {
            controllerPath = controllerPath.tokenize(PARAMETERS_PREFIX)[0]
        }
        controllerPath
    }

}
