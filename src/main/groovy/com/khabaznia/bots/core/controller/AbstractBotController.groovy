package com.khabaznia.bots.core.controller

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.event.DeleteMessagesEvent
import com.khabaznia.bots.core.event.ExecuteMethodsEvent
import com.khabaznia.bots.core.service.UpdateService
import com.khabaznia.bots.core.service.UserService
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.trait.BotControllerBaseRequests
import com.khabaznia.bots.core.trait.Loggable
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
