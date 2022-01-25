package com.khabaznia.bot.endpoint

import com.khabaznia.bot.core.handler.MessageToCommandMapper
import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.event.SendChatActionEvent
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Logged
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


@Slf4j
@RestController
class FrontController implements Logged {

    @Autowired
    Environment env
    @Autowired
    MessageToCommandMapper commandMapper
    @Autowired
    ApplicationEventPublisher publisher
    @Autowired
    UpdateService updateService

    @PostMapping('${bot.token}')
    processUpdate(@RequestBody Update update) {
        log(updateService.getMessageFromUpdate(update))
        log.trace "Got update -> $update"
        def botController = commandMapper.getController(update)
        while (botController) {
            publisher.publishEvent new SendChatActionEvent(actionType: botController.metaData.actionType)
            def path = botController.process update
            botController = path ? commandMapper.getController(path) : null
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity handleRestClientException(AccessDeniedException e) {
        log.error e.message
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(RestClientException.class)
    ResponseEntity handleRestClientException(RestClientException e) {
        e.printStackTrace()
        log.error e.message
        botLog("Exception occured -> $e.message", LogType.WARN)
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(TelegramApiException.class)
    ResponseEntity handleBotException(TelegramApiException e) {
        e.printStackTrace()
        log.error e.message
        botLog("Exception occured -> $e.message", LogType.WARN)
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity handleException(Exception e) {
        e.printStackTrace()
        log.error e.message
        botLog("Exception occured -> $e.message", LogType.WARN)
        new ResponseEntity(HttpStatus.OK)
    }
}