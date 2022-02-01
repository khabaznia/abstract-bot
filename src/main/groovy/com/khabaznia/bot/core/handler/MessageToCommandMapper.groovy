package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.enums.UpdateType
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bot.core.Constants.*
import static com.khabaznia.bot.service.UpdateService.getUpdateType

@Slf4j
@Component
class MessageToCommandMapper {

    @Autowired
    private BotControllerContainer controllerContainer
    @Autowired
    private UpdateService updateService

    BotControllerProxy getController(Update update) {
        getControllerInternal(getPathForStringUpdate(update))
    }

    BotControllerProxy getController(String path) {
        getControllerInternal(path)
    }

    private String getPathForStringUpdate(Update update) {
        def controllerPath = updateService.getMessageFromUpdate(update) ?: getUpdateType(update).defaultController
        log.info 'Request message -> {}', controllerPath
        if (controllerPath?.startsWith(COMMANDS_DELIMITER)) {
            controllerPath = controllerPath.tokenize(PARAMETERS_PREFIX)[0]
        }
        controllerPath
    }

    private BotControllerProxy getControllerInternal(String path) {
        def emojiFromPath = controllerContainer.emojiList.find { path.endsWith(it) }
        def pathWithoutEmoji = emojiFromPath ? path.tokenize(emojiFromPath)[0].strip() : path
        log.info 'Try to find controller for path {}', pathWithoutEmoji
        def pathMatchingControllers = getMatchingControllers(pathWithoutEmoji)
        getDefiniteController(pathMatchingControllers)
    }

    private Map<String, BotControllerProxy> getMatchingControllers(String pathWithoutEmoji) {
        def pathMatchingControllers = controllerContainer.getMatchingControllers(pathWithoutEmoji)
        if (pathMatchingControllers.isEmpty()) {
            log.debug "Controller not found. Try to find $UpdateType.MESSAGE.defaultController controller that should match any user input"
            pathMatchingControllers = controllerContainer.getMatchingControllers(UpdateType.MESSAGE.defaultController)
        }
        pathMatchingControllers
    }

    private BotControllerProxy getDefiniteController(Map<String, BotControllerProxy> matchingControllers) {
        log.trace 'Matching controllers -> {}', matchingControllers*.key
        def result = !matchingControllers ? null :
                (onlyOneController(matchingControllers) && isCommand(matchingControllers))
                        ? matchingControllers.entrySet()[0].value
                        : getControllerByLastAction(matchingControllers)
        result ?: controllerContainer.defaultController
    }

    private static BotControllerProxy getControllerByLastAction(Map<String, BotControllerProxy> matchingControllers) {
        def lastAction = SessionUtil.currentChat.lastAction
        log.trace 'Try to get controller by lastAction {}', lastAction
        matchingControllers.find({ it.key ==~ /$lastAction\$PREVIOUS_PATH_DELIMITER.*/ })?.value
    }

    private static boolean onlyOneController(Map<String, BotControllerProxy> matchingControllers) {
        matchingControllers.size() == 1
    }

    private static boolean isCommand(Map<String, BotControllerProxy> matchingControllers) {
        matchingControllers?.entrySet()[0]?.key?.startsWith(PREVIOUS_PATH_DELIMITER)
    }

}
