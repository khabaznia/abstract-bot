package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.ControllerGenerationException
import com.khabaznia.bot.core.proxy.BotControllerProxy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.*
import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT

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

    BotControllerProxy getController(String previousPath, String currentPath) {
        def fullPath = previousPath + PREVIOUS_PATH_DELIMITER + currentPath
        log.info 'Request ---------------> {}', fullPath
        controllerMap.any { it.key == fullPath }
                ? controllerMap.find { it.key == fullPath }?.value
                : controllerMap.find { controllerForCurrentPath(it.key, currentPath) }?.value
                ?: defaultController
    }

    BotControllerProxy getDefaultController() {
        controllerMap[PREVIOUS_PATH_DELIMITER + DEFAULT]
    }

    private static boolean controllerForCurrentPath(String key, String currentPath) {
        key.startsWith(PREVIOUS_PATH_DELIMITER) && key.substring(1) == currentPath
    }
}
