package com.khabaznia.bot.sender

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions

@Component
class ApiMethodSender extends DefaultAbsSender {

    @Autowired
    Environment env

    ApiMethodSender(DefaultBotOptions options) {
        super(options)
    }

    @Override
    String getBotToken() {
        env.getProperty("bot.token")
    }
}
