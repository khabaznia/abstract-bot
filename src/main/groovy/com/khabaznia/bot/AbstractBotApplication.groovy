package com.khabaznia.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.khabaznia.bot"])
@EnableJpaRepositories('com.khabaznia.bot.repository')
class AbstractBotApplication {

    static void main(String[] args) {
        SpringApplication.run(AbstractBotApplication, args)
    }

}
