package com.khabaznia.bot.sender

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
            return


        long currentTime = System.currentTimeMillis()
        queuesWithRequests = []

        queueContainer.requestsMap.collect { it.value }.each {
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
        def requestToExecute = queuesWithRequests?.find()?.getRequest(currentTime)
        if (requestToExecute != null) {
            log.trace 'Send request job - execution request: {} - {}', requestToExecute.class.simpleName, requestToExecute.chatId
            botRequestService.execute(requestToExecute)
        }
    }
}