package com.khabaznia.bots.example.service

import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.service.BotMessagesService
import org.springframework.stereotype.Component

@Component
class ExampleMessagesService extends BotMessagesService {

    BaseRequest simpleJobMessage(String chatId) {
        sendMessage
                .text('This message from example job')
                .chatId(chatId)
    }
}
