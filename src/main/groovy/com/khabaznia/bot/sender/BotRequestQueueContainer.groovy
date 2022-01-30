package com.khabaznia.bot.sender

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@Component
@Scope(value = 'singleton')
class BotRequestQueueContainer {

    AtomicBoolean hasRequest = new AtomicBoolean(false)
    final ConcurrentHashMap<String, BotRequestQueue> requestsMap = new ConcurrentHashMap<>(32, 0.75f, 1)
}
