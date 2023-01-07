package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'saveMessageRequestProcessingStrategy')
class SaveMessageRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {
    // Executing logic from abstract RequestProcessingStrategy
}
