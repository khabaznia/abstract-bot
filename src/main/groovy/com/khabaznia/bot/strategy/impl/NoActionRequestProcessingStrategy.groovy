package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Component(value = 'noActionRequestProcessingStrategy')
class NoActionRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {
}
