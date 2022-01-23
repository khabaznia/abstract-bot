package com.khabaznia.bot.strategy

import com.khabaznia.bot.model.Button
import com.khabaznia.bot.service.MessageService
import org.springframework.beans.factory.annotation.Autowired

abstract class ButtonProcessingStrategy {

    @Autowired
    MessageService messageService

    abstract void processOnClick(Button button)
}
