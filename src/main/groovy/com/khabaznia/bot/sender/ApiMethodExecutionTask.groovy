package com.khabaznia.bot.sender

import com.khabaznia.bot.enums.BotRequestQueueState
import com.khabaznia.bot.service.BotRequestService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class ApiMethodExecutionTask {

    @Autowired
    private BotRequestQueueContainer queueContainer
    @Autowired
    private BotRequestService botRequestService

    private List<BotRequestQueue> queuesWithRequests

    @Scheduled(fixedRateString = '${requests.per.second}')
    void executeRequestTask() {
        if (!queueContainer.hasRequest.getAndSet(false))
            return // Don't check if no requests were found in previous run.

        long currentTime = System.currentTimeMillis()
        fillListWithReadyQueues(currentTime)

        def requestToExecute = queuesWithRequests?.find()?.getRequest(currentTime)
        log.trace 'Send request job - execution request: {} - {}', requestToExecute?.class?.simpleName, requestToExecute?.chatId
        requestToExecute == null ?: botRequestService.sendToApi(requestToExecute)
    }

    private void fillListWithReadyQueues(long currentTime) {
        queuesWithRequests = []
        queueContainer.requestsMap.collect { it.value }
                .each { logQueueState(it, currentTime) }
                .each {
                    switch (it.getState(currentTime)) {
                        case BotRequestQueueState.INACTIVE:
                            queueContainer.requestsMap.remove(it)
                            break
                        case BotRequestQueueState.READY:
                            queuesWithRequests << it
                        case BotRequestQueueState.WAIT:
                            queueContainer.hasRequest.set(true)
                    }
                }
        queuesWithRequests.sort(Comparator.comparingLong(BotRequestQueue::getLastPutTime))
    }

    private static logQueueState(BotRequestQueue it, long currentTime) {
        log.trace('Queue for chat {}: status - {}, waiting messages for - {}', it.chatId, it.getState(currentTime), it.size())
    }
}