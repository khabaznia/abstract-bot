package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.meta.container.DefaultRequestContainer
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Loggable
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
