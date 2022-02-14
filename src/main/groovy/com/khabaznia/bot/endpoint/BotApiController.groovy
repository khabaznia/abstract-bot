package com.khabaznia.bot.endpoint

import com.khabaznia.bot.exception.BotExecutionApiMethodException
import com.khabaznia.bot.handler.UpdateHandler
import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import org.telegram.telegrambots.meta.api.objects.Update

import java.lang.reflect.UndeclaredThrowableException

import static com.khabaznia.bot.exception.ExceptionUtil.getMessageFromUndeclaredThrowableException

@Slf4j
@RestController
class BotApiController implements Loggable {

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