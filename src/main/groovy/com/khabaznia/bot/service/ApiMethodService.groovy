package com.khabaznia.bot.service


import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.mapper.ResponseMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.service.sender.Sender
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
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

    BaseResponse execute(BaseRequest request) {
        log.debug "Execution api method..."
        try {
            sleep(1000)
            return getMappedResponse(sender.execute(getApiMethod(request)))
        } catch (Exception ex) {
            log.error 'Method failed to execute -> {}', request
            ex.printStackTrace()
        }
        return null
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
