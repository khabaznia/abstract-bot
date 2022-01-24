package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.ControllerGenerationException
import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.lang.reflect.Modifier

import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT
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
        log.info 'Emoji from path ---------------> {}', emoji
        def pathWithoutEmoji = emoji ? currentPath.split(emoji)[0].strip() : currentPath
        log.info 'Try to find controller for path ---------------> {}', pathWithoutEmoji
        controllerMap.find { it.key ==~ /.*\|$pathWithoutEmoji/ }?.value ?: defaultController
    }

    BotControllerProxy getDefaultController() {
        log.info "Controller not found. Using $DEFAULT controller"
        controllerMap[PREVIOUS_PATH_DELIMITER + DEFAULT]
    }
}
