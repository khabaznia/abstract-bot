package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.exception.BotExecutionApiMethodException
import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.mapper.ResponseMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.sender.ApiMethodSender
import com.khabaznia.bot.sender.BotRequestQueueContainer
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

import javax.validation.constraints.NotNull


@Slf4j
@Service
class BotRequestService {

    @Autowired
    private ApplicationContext context
    @Autowired
    private ApiMethodSender sender
    @Autowired
    private RequestMapper requestMapper
    @Autowired
    private ResponseMapper responseMapper
    @Autowired
    private Map<MessageType, RequestProcessingStrategy> requestProcessingStrategyMap
    @Autowired
    private BotRequestQueueContainer queueContainer


    void executeInQueue(@NotNull BaseRequest request) {
        def chatId = request.chatId
        def queue = queueContainer?.requestsMap?.containsKey(chatId)
                ? queueContainer.requestsMap.get(request.chatId)
                : context.getBean('botRequestQueue').chatId(chatId)
        queue.putRequest(request)
        queueContainer.requestsMap.putIfAbsent(chatId, queue)
        queueContainer.hasRequest.set(true)
    }

    void execute(@NotNull BaseRequest request) {
        log.trace "Execution api method..."
        try {
            def response = mapAndExecute(request)
            if (request.relatedMessageUid) {
                response.setRelatedMessageUid(request.relatedMessageUid)
                requestProcessingStrategyMap.get(request.type).processResponse(response)
            }
        } catch (Exception e) {
            log.error 'Method failed to execute -> {}', request
            processToManyRequests(e.message, request)
            throw new BotExecutionApiMethodException("Api method failed to execute -> $e.message", e)
        }
    }

    void processToManyRequests(String errorMessage, BaseRequest request){
        if (errorMessage ==~ /\[429].*/){
            log.error 'To many requests. Send request back to queue'
            executeInQueue(request)
        }
    }

    BaseResponse mapAndExecute(BaseRequest request) {
        getMappedResponse(sender.execute(getApiMethod(request)))
    }

    private BotApiMethod getApiMethod(BaseRequest request) {
        def botApiMethod = requestMapper.toApiMethod(request)
        log.debug "Request after mapping -> {}", botApiMethod
        botApiMethod
    }

    private BaseResponse getMappedResponse(Serializable apiResponse) {
        log.debug "Got response -> $apiResponse"
        responseMapper.toResponse(apiResponse)
    }
}
