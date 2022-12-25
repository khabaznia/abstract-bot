package com.khabaznia.bot.controller

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.event.DeleteMessagesEvent
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.trait.BotControllerBaseRequests
import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
abstract class AbstractBotController implements Configurable, Loggable, BotControllerBaseRequests {


    @Autowired
    protected ApplicationEventPublisher publisher
    @Autowired
    protected UpdateService updateService
    @Autowired
    protected UserService userService

    protected Update update

    void before(Update update) {
        setUp(update)
    }

    void after(String originalPath) {
        def requests = getRequestList()
        requests.each { it.setUpdateId(update.getUpdateId()) }
        publisher.publishEvent new ExecuteMethodsEvent(requests: requests)
        userService.setPreviousPath originalPath
    }

    protected void deleteOldMessages(List<MessageFeature> types) {
        publisher.publishEvent new DeleteMessagesEvent(types: types, updateId: update.getUpdateId())
    }

    private void setUp(Update update) {
        cleanRequests()
        this.update = update
    }
}
