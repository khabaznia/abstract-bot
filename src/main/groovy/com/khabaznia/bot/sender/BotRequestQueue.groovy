package com.khabaznia.bot.sender

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.trait.Configured
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

import static com.khabaznia.bot.core.Constants.*

@Slf4j
@Component(value = 'botRequestQueue')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BotRequestQueue implements Configured {

    String chatId
    long lastSendTime
    private volatile long lastPutTime
    private final ConcurrentLinkedQueue<Long> sendRequestTime = new ConcurrentLinkedQueue<>()

    final ConcurrentLinkedQueue<BaseRequest> requestQueue = new ConcurrentLinkedQueue<>()
    private Map<BotRequestQueueState, List<Closure<Boolean>>> requestQueueStateMap = [
            (BotRequestQueueState.READY)   : [isQueueNotEmpty(), isExpiredLimitBetweenMessages(), isExpiredLimitForSingleChatPerMinute()],
            (BotRequestQueueState.INACTIVE): [isQueueEmpty(), isChatInactive()],
            (BotRequestQueueState.EMPTY)   : [isQueueEmpty()],
    ]

    def isQueueEmpty() {
        (interval) -> { requestQueue.isEmpty() }
    }

    def isQueueNotEmpty() {
        (interval) -> { !requestQueue.isEmpty() }
    }

    def isExpiredLimitBetweenMessages() {
        (interval) -> { (interval - lastSendTime) > getLongConfig(REQUESTS_DELAY_LIMIT_IN_SINGLE_CHAT) }
    }

    def isChatInactive() {
        (interval) -> { (interval - lastSendTime) > getLongConfig(CHAT_INACTIVE_MINUTES) * 60 * 1000 }
    }

    def isExpiredLimitForSingleChatPerMinute() {
        (interval) -> {
            sendRequestTime.size().toLong() <= getLongConfig(REQUESTS_LIMIT_PER_MINUTE_IN_SINGLE_CHAT) ||
                    interval > ((sendRequestTime.peek() ?: 0) + (60 * 1000))
        }
    }


    synchronized void putRequest(BaseRequest request) {
        requestQueue.add(request)
        lastPutTime = System.currentTimeMillis()
    }

    synchronized BaseRequest getRequest(long currentTime) {
        lastSendTime = currentTime
        sendRequestTime.add(lastSendTime)
        if (sendRequestTime.size() >= getLongConfig(REQUESTS_LIMIT_PER_MINUTE_IN_SINGLE_CHAT)) {
            sendRequestTime.poll()
        }
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

}
