package com.khabaznia.bots.core.endpoint

import com.khabaznia.bots.core.exception.BotExecutionApiMethodException
import com.khabaznia.bots.core.exception.BotServiceException
import com.khabaznia.bots.core.handler.UpdateHandler
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.objects.Update

import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bots.core.exception.ExceptionUtil.*

@Slf4j
@RestController
class BotApiController implements Loggable, Configurable {

    @Autowired
    private UpdateHandler updateHandler

    @PostMapping('${env.only.bot.token}')
    processUpdate(@RequestBody Update update) {
        updateHandler.before(update)
        updateHandler.process(update)
        updateHandler.after(update)
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity handleRestClientException(AccessDeniedException e) {
        log.error e.message
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    ResponseEntity handleUndeclaredThrowableException(UndeclaredThrowableException ex) {
        if (isBotServiceException(ex)) {
            updateHandler.handleServiceException(getTargetException(ex) as BotServiceException)
        } else {
            ex.printStackTrace()
            def message = getMessageFromUndeclaredThrowableException(ex)
            log.error message
            sendWarnLog "Exception: $message"
        }
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