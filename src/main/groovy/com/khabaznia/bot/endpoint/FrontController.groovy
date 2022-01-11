package com.khabaznia.bot.endpoint

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClientException
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Slf4j
@RestController
class FrontController {

    @Autowired
    Environment env

    @PostMapping('${bot.token}')
    processUpdate(@RequestBody Update update) {
        log.trace "Got update -> $update"


    }


    @ExceptionHandler(RestClientException.class)
    ResponseEntity handleRestClientException(final RestClientException e) {
        e.printStackTrace()
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(TelegramApiException.class)
    ResponseEntity handleBotException(final TelegramApiException e) {
        e.printStackTrace()
        new ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity handleException(final Exception e) {
        e.printStackTrace()
        new ResponseEntity(HttpStatus.OK)
    }
}