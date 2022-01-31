package com.khabaznia.bot.endpoint

import com.khabaznia.bot.core.handler.MessageToCommandMapper
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.event.SendChatActionEvent
import com.khabaznia.bot.exception.BotExecutionApiMethodException
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Loggable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClientException
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE_ATTR
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID_ATTR

import static com.khabaznia.bot.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException


@Slf4j
@RestController
class FrontController implements Loggable {

    @Autowired
    private Environment env
    @Autowired
    private MessageToCommandMapper commandMapper
    @Autowired
    private ApplicationEventPublisher publisher
    @Autowired
    private UpdateService updateService

    @PostMapping('${bot.token}')
    processUpdate(@RequestBody Update update) {
        log(update)
        setUpdateMessageToSession(update)
        sendLog(new LogEvent(text: updateService.getMessageFromUpdate(update), logType: LogType.DEBUG))
        def botController = commandMapper.getController(update)
        while (botController) {
            publisher.publishEvent new SendChatActionEvent(actionType: botController.metaData.actionType)
            def path = botController.process update
            botController = path ? commandMapper.getController(path) : null
        }
    }

    private static log(Update update) {
        log.info '====================================================================================='
        log.debug 'Got update with id {}. Has message -> {}', update.updateId, update.hasMessage()
        log.trace "Full update -> $update"
    }

    private setUpdateMessageToSession(Update update) {
        SessionUtil.setAttribute(UPDATE_MESSAGE_ATTR, updateService.getMessageFromUpdate(update))
        SessionUtil.setAttribute(UPDATE_ID_ATTR, update.updateId.toString())
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity handleRestClientException(AccessDeniedException e) {
        log.error e.message
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    ResponseEntity handleUndeclaredThrowableException(UndeclaredThrowableException e) {
        e.printStackTrace()
        def message = getMessageFromUndeclaredThrowableException(e)
        log.error message
        sendWarnLog "Exception: $message"
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(BotExecutionApiMethodException.class)
    ResponseEntity handleException(BotExecutionApiMethodException e) {
        e.printStackTrace()
        log.error e.message
        sendWarnLog "BotExcecutionException: $e.message"
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity handleException(Exception e) {
        e.printStackTrace()
        log.error e.message
        sendWarnLog "Exception: $e.message"
        new ResponseEntity(HttpStatus.OK)
    }
}