package com.khabaznia.bot.service


import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.mapper.ResponseMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.service.sender.Sender
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod


@Slf4j
@Service
class ApiMethodService {

    @Autowired
    Sender sender
    @Autowired
    RequestMapper requestMapper
    @Autowired
    ResponseMapper responseMapper


    BaseResponse execute(BaseRequest request){
        log.debug "Execution api method..."
        getMappedResponse(sender.execute(getApiMethod(request)), request)
    }

    private BotApiMethod getApiMethod(BaseRequest request) {
        log.debug "Request before mapping -> $request"
        def botApiMethod = requestMapper.toApiMethod(request)
        log.debug "Request after mapping -> {}", botApiMethod
        botApiMethod
    }

    private BaseResponse getMappedResponse(Serializable apiResponse, BaseRequest request) {
        log.debug "Got response -> $apiResponse"
        def response = responseMapper.toResponse(apiResponse, request.type)
        log.debug "Response after mapping -> {}", response
        response
    }
}
