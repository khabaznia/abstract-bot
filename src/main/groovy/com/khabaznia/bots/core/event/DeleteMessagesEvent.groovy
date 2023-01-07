package com.khabaznia.bots.core.event

import com.khabaznia.bots.core.enums.MessageFeature

class DeleteMessagesEvent {

    List<MessageFeature> types
    Integer updateId
}
