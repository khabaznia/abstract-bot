package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.ControllerGenerationException
import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.meta.Emoji
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.lang.reflect.Modifier

import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
import static com.khabaznia.bot.controller.Constants.COMMON.EMPTY_PATH
import static com.khabaznia.bot.core.Constants.PREVIOUS_PATH_DELIMITER

@Slf4j
@Component
class BotControllerContainer {

    Map<String, BotControllerProxy> controllerMap
    List<String> emojiList

    BotControllerContainer() {
        controllerMap = [:]
        emojiList = Emoji.declaredFields.findAll { Modifier.isStatic(it.getModifiers()) }
                .each { it.setAccessible(true) }
                .collect { it.get(Emoji.class) as String }
                .findAll { it != null }
                .findAll { !it.matches(/[a-zA-Z]*/) }
        log.trace 'Emoji list: {}', emojiList
    }

    void addController(BotControllerProxy proxy) {
        def path = proxy.metaData.controllerPath
        if (controllerMap.containsKey(path))
            throw new ControllerGenerationException("Controller for path -> $path is already exists")
        log.info 'Added new controller for path -> {}', path
        controllerMap[path] = proxy
    }

    BotControllerProxy getController(String currentPath) {
        def emoji = emojiList.find { currentPath.endsWith(it) }
        log.debug 'Emoji from path ---------------> {}', emoji
        def pathWithoutEmoji = emoji ? currentPath.tokenize(emoji)[0].strip() : currentPath
        log.info 'Try to find controller for path ---------------> {}', pathWithoutEmoji
        def pathMatchingControllers = controllerMap.findAll { it.key ==~ /.*\$PREVIOUS_PATH_DELIMITER$pathWithoutEmoji/ }
        if (pathMatchingControllers.isEmpty()) {
            log.debug "Controller not found. Try to find $EMPTY_PATH controller that should match any user input"
            pathMatchingControllers = controllerMap.findAll { it.key ==~ /.*\$PREVIOUS_PATH_DELIMITER$EMPTY_PATH/ }
        }
        getControllerFromMatching(pathMatchingControllers)
    }

    BotControllerProxy getControllerFromMatching(Map<String, BotControllerProxy> matchingControllers) {
        log.trace 'Matching controllers -> {}', matchingControllers*.key
        def result = null
        if (matchingControllers) {
            if (matchingControllers.size() == 1 && matchingControllers?.entrySet()[0]?.key?.startsWith(PREVIOUS_PATH_DELIMITER)) {
                result = matchingControllers.entrySet()[0].value
            } else {
                def lastAction = SessionUtil.currentChat.lastAction
                log.trace 'Try to get controller by lastAction {}', lastAction
                result = matchingControllers.find({ it.key ==~ /$lastAction\$PREVIOUS_PATH_DELIMITER.*/ })?.value
            }
        }
        result ?: defaultController

    }

    private BotControllerProxy getDefaultController() {
        log.info "Controller not found. Using $DEFAULT controller"
        controllerMap[PREVIOUS_PATH_DELIMITER + DEFAULT]
    }
}
