package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'saveMessageRequestProcessingStrategy')
class SaveMessageRequestProcessingStrategy extends RequestProcessingStrategy<BaseRequest, BaseResponse> {

    @Autowired
    MessageService messageService

    @Override
    void afterProcess(BaseResponse response) {
        if (response instanceof MessageResponse) {
            log.debug 'Saving message with messageId: {}', response.result.messageId
            messageService.saveMessage(response.result)
        }
    }
}
