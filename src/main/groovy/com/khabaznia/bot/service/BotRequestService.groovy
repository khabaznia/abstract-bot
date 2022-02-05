package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.exception.BotExecutionApiMethodException
import com.khabaznia.bot.meta.mapper.ResponseMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.DeleteMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.sender.ApiMethodSender
import com.khabaznia.bot.sender.BotRequestQueue
import com.khabaznia.bot.sender.BotRequestQueueContainer
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import static com.khabaznia.bot.core.Constants.EXECUTE_REQUESTS_IN_QUEUE

@Slf4j
@Service
class BotRequestService implements Configurable {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ApiMethodSender sender
    @Autowired
    private Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private BotRequestQueueContainer queueContainer
    @Autowired
    private MessageService messageService

    void execute(BaseRequest request) {
        execute(request, isEnabled(EXECUTE_REQUESTS_IN_QUEUE))
    }

    void execute(BaseRequest request, Boolean useQueue) {
        requestProcessingStrategyMap.get(request.type).updateWithMappedApiMethod(request)
        useQueue ? executeInQueue(request) : sendToApi(request)
    }

    private void executeInQueue(BaseRequest request) {
        if (request == null) return
        def queue = getQueueForChat(request.chatId)
        putRequestToQueue(queue, request)
    }

    private void executeInQueueWithLimit(BaseRequest request, long limit) {
        if (request == null) return
        def queue = getQueueForChat(request.chatId)
        queue.setToManyRequestsLimit(limit)
        putRequestToQueue(queue, request)
    }

    void sendToApi(BaseRequest request) {
        if (request == null) return
        try {
            log.info 'Sending api request: type: {}, chat: {}, class: {}. {}', request.type, request.chatId,
                    request.class.simpleName, request.relatedMessageUid ? "Related message: $request.relatedMessageUid" : ''
            log.debug 'Send request: {}', request
            def response = executeMapped(request)
            if (request.relatedMessageUid) {
                response.setRelatedMessageUid(request.relatedMessageUid)
                requestProcessingStrategyMap.get(request.type).processResponse(response)
            }
        } catch (Exception e) {
            handleException(e, request)
        }
    }

    BaseResponse executeMapped(BaseRequest request) {
        getMappedResponse(sender.execute(request.apiMethod))
    }

    private void handleException(Exception e, BaseRequest request) {
        log.error 'Method failed to execute -> {}', request.apiMethod.toString()
        if (e.message ==~ /.*\[429].*/) {
            log.warn 'To many requests. Send request back to queue'
            def limit = getLimitFromMessage(e.message)
            executeInQueueWithLimit(request, limit)
        } else if (e.message ==~ /.*\[400].*message to delete not found.*/) {
            if (request instanceof DeleteMessage && request.getMessageId()) {
                log.warn 'Message was deleted by another . Dropping from DB.'
                messageService.removeMessage(request.messageId.toString())
            }
        } else {
            throw new BotExecutionApiMethodException("Api method failed to execute: $e.message", e)
        }
    }

    private void putRequestToQueue(BotRequestQueue queue, BaseRequest request) {
        log.info 'Put request to queue of chat {}. {}', request.chatId, request.class.simpleName
        queue.putRequest(request)
        queueContainer.requestsMap.putIfAbsent(request.chatId, queue)
        queueContainer.hasRequest.set(true)
    }

    private BotRequestQueue getQueueForChat(String chatId) {
        queueContainer?.requestsMap?.containsKey(chatId)
                ? queueContainer.requestsMap.get(chatId)
                : context.getBean('botRequestQueue').chatId(chatId)
    }

    private static long getLimitFromMessage(String errorMessage) {
        def result = 30
        try {
            def retrySeconds = errorMessage.split('retry after ')[1]?.strip()
            result = Long.parseLong(retrySeconds)
        } catch (NumberFormatException e) {
            log.warn 'Can\'t parse real limit from API. Set {} seconds', result
        }
        result
    }

    private static BaseResponse getMappedResponse(Serializable apiResponse) {
        log.debug "Got response -> $apiResponse"
        ResponseMapper.toResponse(apiResponse)
    }
}
