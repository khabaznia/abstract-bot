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
import com.khabaznia.bot.sender.WrappedRequestEntity
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import static com.khabaznia.bot.core.Constants.COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS
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
        if (request) {
            def wrappedRequest = new WrappedRequestEntity(request: request,
                    botApiMethod: getRequestApiMethod(request),
                    countOfRetries: countOfRetries)
            useQueue ? executeInQueue(wrappedRequest) : sendToApi(wrappedRequest)
        }
    }

    private void executeInQueue(WrappedRequestEntity wrappedRequest) {
        if (wrappedRequest == null) return
        def queue = getQueueForChat(wrappedRequest.request.chatId)
        putRequestToQueue(queue, wrappedRequest)
    }

    private void executeInQueueWithLimit(WrappedRequestEntity wrappedRequest, long limit) {
        if (wrappedRequest == null) return
        def queue = getQueueForChat(wrappedRequest.request.chatId)
        queue.setToManyRequestsLimit(limit)
        putRequestToQueue(queue, wrappedRequest)
    }

    void sendToApi(WrappedRequestEntity wrappedRequest) {
        if (!wrappedRequest && !wrappedRequest.request && !wrappedRequest.botApiMethod) return
        try {
            log.info 'Sending api request: type: {}, chat: {}, class: {}. {}', wrappedRequest.request.type, wrappedRequest.request.chatId,
                    wrappedRequest.request.class.simpleName, wrappedRequest.request.relatedMessageUid ? "Related message: $wrappedRequest.request.relatedMessageUid" : ''
            log.debug 'Send request: {}', wrappedRequest.request
            wrappedRequest.countOfRetries--
            def response = executeMapped(wrappedRequest)
            if (wrappedRequest.request.relatedMessageUid) {
                response.setRelatedMessageUid(wrappedRequest.request.relatedMessageUid)
                requestProcessingStrategyMap.get(wrappedRequest.request.type).processResponse(response)
            }
        } catch (Exception e) {
            handleException(e, wrappedRequest)
        }
    }

    BaseResponse executeMapped(WrappedRequestEntity wrappedRequest) {
        getMappedResponse(sender.execute(wrappedRequest.botApiMethod))
    }

    private Object getRequestApiMethod(BaseRequest request) {
        requestProcessingStrategyMap.get(request.type).getMappedApiMethod(request)
    }

    private int getCountOfRetries() {
        getIntConfig(COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS)
    }

    private void handleException(Exception e, WrappedRequestEntity wrappedRequest) {
        log.error 'Method failed to execute -> {}, countOfEntriesLast: {}', wrappedRequest.botApiMethod.toString(), wrappedRequest.countOfRetries

        if (e.message ==~ /.*\[429].*/) {
            log.warn 'To many requests. Send request back to queue'
            def limit = getLimitFromMessage(e.message)
            executeInQueueWithLimit(wrappedRequest, limit)
        } else if (e.message ==~ /.*\[400].*message to delete not found.*/) {
            def request = wrappedRequest.request
            if (request instanceof DeleteMessage && request.messageId) {
                log.warn 'Message was deleted by another . Dropping from DB.'
                messageService.removeMessage(request.messageId.toString())
            }
        } else {
            throw new BotExecutionApiMethodException("Api method failed to execute: $e.message", e)
        }

    }

    private void putRequestToQueue(BotRequestQueue queue, WrappedRequestEntity wrappedRequest) {
        log.info 'Put request to queue of chat {}. {}, retires: ', wrappedRequest.request.chatId, wrappedRequest.request.class.simpleName, wrappedRequest.countOfRetries
        if (wrappedRequest.countOfRetries != 0) {
            queue.putRequest(wrappedRequest)
            queueContainer.requestsMap.putIfAbsent(wrappedRequest.request.chatId, queue)
            queueContainer.hasRequest.set(true)
        }
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
