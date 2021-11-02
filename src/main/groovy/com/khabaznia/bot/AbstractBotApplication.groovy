package com.khabaznia.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.telegram.telegrambots.bots.DefaultAbsSender

@SpringBootApplication
class AbstractBotApplication {

    static void main(String[] args) {
        SpringApplication.run(AbstractBotApplication, args)
    }

}
