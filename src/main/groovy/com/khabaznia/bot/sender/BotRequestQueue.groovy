package com.khabaznia.bot.sender

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.trait.Configured
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

import static com.khabaznia.bot.core.Constants.*

@Slf4j
@Component(value = 'botRequestQueue')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BotRequestQueue implements Configured {

    String chatId
    private volatile long lastSendTime
    private volatile long toManyRequestsLimit
    private volatile long lastPutTime

    final ConcurrentLinkedQueue<BaseRequest> requestQueue = new ConcurrentLinkedQueue<>()
    private Map<BotRequestQueueState, List<Closure<Boolean>>> requestQueueStateMap = [
            (BotRequestQueueState.READY)   : [isQueueNotEmpty(), isExpiredLimitBetweenMessages(), isExpiredLimitForSingleChatPerMinute(), isToManyRequestLimitExpired()],
            (BotRequestQueueState.INACTIVE): [isQueueEmpty(), isChatInactive()],
            (BotRequestQueueState.EMPTY)   : [isQueueEmpty()],
    ]

    def isQueueEmpty() {
        (callTime) -> { requestQueue.isEmpty() }
    }

    def isQueueNotEmpty() {
        (callTime) -> { !requestQueue.isEmpty() }
    }

    def isExpiredLimitBetweenMessages() {
        (callTime) -> { (callTime - lastSendTime) > getLongConfig(REQUESTS_DELAY_LIMIT_IN_SINGLE_CHAT) }
    }

    def isChatInactive() {
        (callTime) -> { (callTime - lastSendTime) > getLongConfig(CHAT_INACTIVE_MINUTES) * 60 * 1000 }
    }

    def isExpiredLimitForSingleChatPerMinute() {
        (callTime) -> {
            log.trace 'Is waiting by chat messages limit {}. Current waiting messages {}', false, requestQueue.size().toLong()
            true
        }
    }

    def isToManyRequestLimitExpired() {
        (callTime) -> { callTime > toManyRequestsLimit }
    }

    synchronized void putRequest(BaseRequest request) {
        requestQueue.offer(request)
        lastPutTime = System.currentTimeMillis()
    }

    synchronized BaseRequest getRequest(long currentTime) {
        lastSendTime = currentTime
        requestQueue.poll()
    }

    synchronized BotRequestQueueState getState(long currentTime) {
        requestQueueStateMap.find {
            it.value.collect { it.call(currentTime) }.every()
        }?.key ?: BotRequestQueueState.WAIT
    }

    Long getLastPutTime() {
        lastPutTime
    }

    void setToManyRequestsLimit(long seconds) {
        log.trace 'To many requests for chat {}. Setting limit to {} seconds', chatId, seconds
        toManyRequestsLimit = (System.currentTimeMillis() + (seconds * 1000))
    }

}
