package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.enums.UpdateType
import com.khabaznia.bot.exception.ControllerGenerationException
import com.khabaznia.bot.meta.Emoji
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.lang.reflect.Modifier

import static com.khabaznia.bot.core.Constants.PREVIOUS_PATH_DELIMITER

@Slf4j
@Component
class BotControllerContainer {

    private Map<String, BotControllerProxy> controllerMap
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
        log.info 'Added controller for path -> {}', path
        controllerMap[path] = proxy
    }

    Map<String, BotControllerProxy> getMatchingControllers(String path) {
        def specialCharRegex = /[\W_&&[^\s]]/;
        def escapedPath = path.replaceAll(specialCharRegex, /\\$0/);
        controllerMap.findAll { it.key ==~ /.*\$PREVIOUS_PATH_DELIMITER$escapedPath/ }
    }

    BotControllerProxy getDefaultController() {
        log.info "Controller not found. Using ${UpdateType.UNDEFINED.defaultController} controller"
        controllerMap[PREVIOUS_PATH_DELIMITER + UpdateType.UNDEFINED.defaultController]
    }
}
