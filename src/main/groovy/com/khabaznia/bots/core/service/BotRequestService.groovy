package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.BotRequestQueueState
import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.exception.BotExecutionApiMethodException
import com.khabaznia.bots.core.meta.mapper.RequestMapper
import com.khabaznia.bots.core.meta.mapper.ResponseMapper
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.request.impl.DeleteMessage
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.sender.ApiMethodSender
import com.khabaznia.bots.core.sender.BotRequestQueue
import com.khabaznia.bots.core.sender.BotRequestQueueContainer
import com.khabaznia.bots.core.sender.WrappedRequestEntity
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import static com.khabaznia.bots.core.routing.Constants.*
import static java.util.Objects.isNull

@Slf4j
@Service
class BotRequestService implements Configurable {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ApiMethodSender sender
    @Autowired
    private Map<MessageFeature, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private BotRequestQueueContainer queueContainer
    @Autowired
    private MessageService messageService
    @Autowired
    private RequestMapper requestMapper

    void execute(BaseRequest request, Boolean useQueue = shouldExecuteInQueue(request)) {
        if (request) {
            def wrappedRequest = new WrappedRequestEntity(request: request,
                    botApiMethod: getRequestApiMethod(request),
                    countOfRetries: countOfRetries)
            useQueue ? executeInQueue(wrappedRequest) : sendToApi(wrappedRequest)
        }
    }

    BaseResponse executeWithResponse(BaseRequest request) {
        !request ? null : sendToApi(new WrappedRequestEntity(request: request,
                botApiMethod: requestMapper.toApiMethod(request),
                countOfRetries: 1))
    }

    BaseResponse sendToApi(WrappedRequestEntity wrappedRequest) {
        if (isNotValid wrappedRequest) return null
        try {
            logRequest(wrappedRequest)
            wrappedRequest.countOfRetries--
            def response = executeMapped(wrappedRequest)
            response.setRelatedMessageUid(wrappedRequest.request.relatedMessageUid)
            wrappedRequest.request.features.each { feature ->
                requestProcessingStrategyMap.get(feature).processResponse(response)
            }
            return response
        } catch (Exception e) {
            handleException(e, wrappedRequest)
        }
        return null
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

    private BaseResponse executeMapped(WrappedRequestEntity wrappedRequest) {
        getMappedResponse(sender.execute(wrappedRequest.botApiMethod))
    }

    private Object getRequestApiMethod(BaseRequest request) {
        request.chatId != null
                ? requestMapper.toApiMethod(request)
                : null
    }

    private int getCountOfRetries() {
        getIntConfig(COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS)
    }

    private void handleException(Exception e, WrappedRequestEntity wrappedRequest) {
        log.error 'Method failed to execute -> {}, countOfEntriesLast: {}', wrappedRequest.botApiMethod.toString(), wrappedRequest.countOfRetries

        if (wrappedRequest.request.isOmissible) {
            log.warn 'Some error during executing omissible request. Just ignore it.'
            log.warn e.message
        } else if (e.message ==~ /.*\[429].*/) {
            log.warn 'To many requests. Send request back to queue'
            def limit = getLimitFromMessage(e.message)
            executeInQueueWithLimit(wrappedRequest, limit)
        } else if (e.message ==~ /.*\[400].*message to delete not found.*/
                || e.message ==~ /.*\[400].*message can't be deleted for everyone.*/) {
            def request = wrappedRequest.request
            if (request instanceof DeleteMessage && (request.messageId || request.label)) {
                log.warn 'Message was deleted by another thread. Dropping from DB.'
                def messageCode = request.messageId.toString() ?: request.label
                messageService.removeMessage(messageCode)
            }
        } else if (e.message ==~ /.*\[403].*bot is not a member of the group chat.*/) {
            log.warn 'Bot is not member of chat. Possibly can be ignored.'
            e.printStackTrace()
        } else {
            throw new BotExecutionApiMethodException("Api method failed to execute: $e.message", e)
        }

    }

    private void putRequestToQueue(BotRequestQueue queue, WrappedRequestEntity wrappedRequest) {
        log.info 'Put request to queue of chat {}. {}, retires: ', wrappedRequest.request.chatId, wrappedRequest.request.class.simpleName, wrappedRequest.countOfRetries
        if (wrappedRequest.countOfRetries != 0) {
            if (!(wrappedRequest.request.isOmissible && isWaitState(queue)))
                queue.putRequest(wrappedRequest)

            queueContainer.requestsMap.putIfAbsent(wrappedRequest.request.chatId, queue)
            queueContainer.hasRequest.set(true)
        }
    }

    private static boolean isWaitState(BotRequestQueue queue) {
        queue.getState(System.currentTimeMillis()) == BotRequestQueueState.WAIT
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
            result = Long.parseLong(retrySeconds) + 5
        } catch (NumberFormatException e) {
            log.warn 'Can\'t parse real limit from API. Set {} seconds', result
        }
        result
    }

    private static BaseResponse getMappedResponse(Serializable apiResponse) {
        log.debug "Got response -> $apiResponse"
        ResponseMapper.toResponse(apiResponse)
    }

    private static void logRequest(WrappedRequestEntity wrappedRequest) {
        log.info 'Sending api request: type: {}, chat: {}, class: {}. {}', wrappedRequest.request.features, wrappedRequest.request.chatId,
                wrappedRequest.request.class.simpleName, wrappedRequest.request.relatedMessageUid ? "Related model: $wrappedRequest.request.relatedMessageUid" : ''
        log.debug 'Send request: {}', wrappedRequest.request
    }

    private static boolean isNotValid(WrappedRequestEntity wrappedRequest) {
        isNull(wrappedRequest) && isNull(wrappedRequest.request) && isNull(wrappedRequest.botApiMethod)
    }

    private boolean shouldExecuteInQueue(BaseRequest request) {
        isEnabled(EXECUTE_REQUESTS_IN_QUEUE) &&
                (isEnabled(EXECUTING_IN_QUEUE_ONLY_FOR_GROUP_CHATS)
                        ? UserService.getChatType(request.chatId) == ChatType.GROUP
                        : true)
    }
}
