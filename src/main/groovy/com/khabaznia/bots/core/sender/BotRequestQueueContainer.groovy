package com.khabaznia.bots.core.sender

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Component
@Scope(value = 'singleton')
class BotRequestQueueContainer {

    static AtomicInteger requestOrder = new AtomicInteger(0)
    AtomicBoolean hasRequest = new AtomicBoolean(false)
    final ConcurrentHashMap<String, BotRequestQueue> requestsMap = new ConcurrentHashMap<>(32, 0.75f, 1)
}
