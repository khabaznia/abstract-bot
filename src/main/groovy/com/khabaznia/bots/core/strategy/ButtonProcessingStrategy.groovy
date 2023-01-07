package com.khabaznia.bots.core.strategy

import com.khabaznia.bots.core.model.Button
import com.khabaznia.bots.core.service.MessageService
import org.springframework.beans.factory.annotation.Autowired

abstract class ButtonProcessingStrategy {

    @Autowired
    protected MessageService messageService

    abstract void processOnClick(Button button)
}
