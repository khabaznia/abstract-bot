package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.exception.BotExecutionApiMethodException
import com.khabaznia.bot.meta.mapper.ResponseMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.sender.ApiMethodSender
import com.khabaznia.bot.sender.BotRequestQueue
import com.khabaznia.bot.sender.BotRequestQueueContainer
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod


@Slf4j
@Service
class BotRequestService {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ApiMethodSender sender
    @Autowired
    private ResponseMapper responseMapper
    @Autowired
    private Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private BotRequestQueueContainer queueContainer

    void executeInQueue(BaseRequest request) {
        if (request == null) return
        def queue = getQueueForChat(request.chatId)
        requestProcessingStrategyMap.get(request.type).updateWithMappedApiMethod(request)
        putRequestToQueue(queue, request)
    }

    void executeInQueueWithLimit(BaseRequest request, long limit) {
        if (request == null) return
        def queue = getQueueForChat(request.chatId)
        queue.setToManyRequestsLimit(limit)
        putRequestToQueue(queue, request)
    }

    private void putRequestToQueue(BotRequestQueue queue, BaseRequest request) {
        log.debug 'Put request to queue of chat {} -> {}', request.chatId, request
        queue.putRequest(request)
        queueContainer.requestsMap.putIfAbsent(request.chatId, queue)
        queueContainer.hasRequest.set(true)
    }

    private BotRequestQueue getQueueForChat(String chatId) {
        queueContainer?.requestsMap?.containsKey(chatId)
                ? queueContainer.requestsMap.get(chatId)
                : context.getBean('botRequestQueue').chatId(chatId)
    }

    void execute(BaseRequest request) {
        if (request == null) return
        try {
            def response = executeMapped(request)
            if (request.relatedMessageUid) {
                response.setRelatedMessageUid(request.relatedMessageUid)
                requestProcessingStrategyMap.get(request.type).processResponse(response)
            }
        } catch (Exception e) {
            log.error 'Method failed to execute -> {}', request
            if (e.message ==~ /.*\[429].*/) {
                log.error 'To many requests. Send request back to queue'
                executeInQueueWithLimit(request, getLimitFromMessage(e.message))
            } else {
                throw new BotExecutionApiMethodException("Api method failed to execute -> $e.message", e)
            }
        }
    }

    private static long getLimitFromMessage(String errorMessage) {
        def result = 30
        try {
            def splited = errorMessage.split('retry after ')[1]?.strip()
            result = Long.parseLong(splited)
        } catch (NumberFormatException ex) {
            log.debug 'Can\'t parse real limit from API. Set {} seconds', result
        }
        result
    }

    BaseResponse executeMapped(BaseRequest request) {
        getMappedResponse(sender.execute(request.apiMethod as BotApiMethod))
    }

    private BaseResponse getMappedResponse(Serializable apiResponse) {
        log.debug "Got response -> $apiResponse"
        responseMapper.toResponse(apiResponse)
    }
}
