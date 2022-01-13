package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.ControllerGenerationException
import com.khabaznia.bot.core.proxy.BotControllerProxy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.core.Constants.PREVIOUS_PATH_DELIMITER

@Slf4j
@Component
class BotControllerContainer {

    Map<String, BotControllerProxy> controllerMap

    BotControllerContainer() {
        controllerMap = new HashMap<>()
    }

    void addController(BotControllerProxy proxy) {
        def path = proxy.metaData.controllerPath
        if (controllerMap.containsKey(path))
            throw new ControllerGenerationException("Controller for path -> $path is already exists")
        log.info 'Added new controller for path -> {}', path
        controllerMap[path] = proxy
    }

    BotControllerProxy getController(String currentPath) {
        log.info 'Try to find controller for path ---------------> {}', currentPath
        controllerMap.find { it.key ==~ /.*\|$currentPath/ }?.value ?: defaultController
    }

    BotControllerProxy getDefaultController() {
        log.info "Controller not found. Using $DEFAULT controller"
        controllerMap[PREVIOUS_PATH_DELIMITER + DEFAULT]
    }
}
