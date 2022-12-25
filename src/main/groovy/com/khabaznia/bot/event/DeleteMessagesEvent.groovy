package com.khabaznia.bot.event

import com.khabaznia.bot.enums.MessageFeature

class DeleteMessagesEvent {

    List<MessageFeature> types
    Integer updateId
}
