package com.khabaznia.bots.core.routing.handler

import com.khabaznia.bots.core.enums.UpdateType
import com.khabaznia.bots.core.routing.proxy.BotControllerProxy
import com.khabaznia.bots.core.service.UpdateService
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.util.BotSession
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bots.core.routing.Constants.*
import static com.khabaznia.bots.core.service.UpdateService.getUpdateType

@Slf4j
@Component
class UpdateBotControllerResolver implements Configurable {

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
        controllerPath = controllerPath.split(BOT_NAME_DELIMITER + getConfig(BOT_NAME))[0]
        controllerPath = controllerPath.tokenize(PARAMETERS_PREFIX)[0]
        controllerPath
    }

    private BotControllerProxy getControllerInternal(String path) {
        def emojiFromPath = controllerContainer.emojiList.find { path.endsWith(it) }
        def pathWithoutEmoji = emojiFromPath ? path.tokenize(emojiFromPath)[0]?.strip() ?: '' : path
        log.info 'Try to find controller for path {}', pathWithoutEmoji
        def pathMatchingControllers = getMatchingControllers(pathWithoutEmoji)
        getDefiniteController(pathMatchingControllers)
    }

    private Map<String, BotControllerProxy> getMatchingControllers(String path) {
        def pathMatchingControllers = findAllMatchingControllers(path)
        if (pathMatchingControllers.isEmpty()) {
            // if no controller found - probably it just some text from user. Try to find matching controller by ROLE or the one that should be after lastAction
            log.debug "Controller not found. Try to find $UpdateType.UNDEFINED_MESSAGE.defaultController controller that should match any user input"
            pathMatchingControllers = findAllMatchingControllers(UpdateType.UNDEFINED_MESSAGE.defaultController)
        }
        pathMatchingControllers
    }

    private Map<String, BotControllerProxy> findAllMatchingControllers(String path) {
        getRoleSpecificControllers(path) ?: controllerContainer.getMatchingControllers(path)
    }

    private Map<String, BotControllerProxy> getRoleSpecificControllers(String path) {
        controllerContainer.getMatchingControllers(path + SPECIFIC_ROLE_DELIMITER + BotSession.currentChat.role.toString())
                ?: controllerContainer.getMatchingControllers(path + SPECIFIC_ROLE_DELIMITER + BotSession.currentUser.role.toString())
    }

    private BotControllerProxy getDefiniteController(Map<String, BotControllerProxy> matchingControllers) {
        log.trace 'Matching controllers -> {}', matchingControllers*.key
        def result = !matchingControllers ? null :
                (onlyOneController(matchingControllers) && isCommand(matchingControllers))
                        ? matchingControllers.values().find()
                        : getControllerByLastAction(matchingControllers)
        result ?: controllerContainer.defaultController
    }

    private static BotControllerProxy getControllerByLastAction(Map<String, BotControllerProxy> matchingControllers) {
        def lastAction = BotSession.currentChat.lastAction
        log.trace 'Try to get controller by lastAction {}', lastAction
        // try to find match by last action
        def controller = matchingControllers.find({ it.key ==~ /$lastAction\$PREVIOUS_PATH_DELIMITER.*/ })
                // if no matching - try to get one that is default (only path without previous path specification)
                ?: matchingControllers.find({ it.key ==~ /\$PREVIOUS_PATH_DELIMITER.*/ })
        controller?.value
    }

    private static boolean onlyOneController(Map<String, BotControllerProxy> matchingControllers) {
        matchingControllers.size() == 1
    }

    private static boolean isCommand(Map<String, BotControllerProxy> matchingControllers) {
        matchingControllers?.entrySet()[0]?.key?.startsWith(PREVIOUS_PATH_DELIMITER)
    }
}
