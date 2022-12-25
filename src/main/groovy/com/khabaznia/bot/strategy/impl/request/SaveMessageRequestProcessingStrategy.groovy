package com.khabaznia.bot.strategy.impl.request

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'saveMessageRequestProcessingStrategy')
class SaveMessageRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {
    // Executing logic from abstract RequestProcessingStrategy
}
