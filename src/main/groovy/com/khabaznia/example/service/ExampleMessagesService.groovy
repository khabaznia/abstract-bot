package com.khabaznia.example.service

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.service.BotMessagesService
import org.springframework.stereotype.Component

@Component
class ExampleMessagesService extends BotMessagesService {

    BaseRequest simpleJobMessage(String chatId) {
        sendMessage
                .text('This message from example job')
                .chatId(chatId)
    }

}
