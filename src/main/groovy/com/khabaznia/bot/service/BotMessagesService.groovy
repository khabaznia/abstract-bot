package com.khabaznia.bot.service

import com.khabaznia.bot.meta.container.DefaultRequestContainer
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.trait.BaseRequests
import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class BotMessagesService implements BaseRequests, Loggable {

    @Autowired
    private DefaultRequestContainer requests

    BaseRequest sendExceptionMessage(String message, Map<String, String> binding) {
        sendMessage.text(message).binding(binding)
    }
}
