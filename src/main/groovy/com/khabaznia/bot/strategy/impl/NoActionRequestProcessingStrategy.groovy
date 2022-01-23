package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'noActionRequestProcessingStrategy')
class NoActionRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Override
    Message beforeProcess(BaseRequest request) {
        log.debug 'No action'
    }

    @Override
    void afterProcess(BaseResponse response, Message message) {
        log.debug 'No action'
    }
}
