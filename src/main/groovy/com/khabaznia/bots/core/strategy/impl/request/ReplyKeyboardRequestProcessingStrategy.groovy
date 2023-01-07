package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import com.khabaznia.bots.core.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID

@Slf4j
@Component(value = 'replyKeyboardRequestProcessingStrategy')
class ReplyKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<AbstractKeyboardMessage, BaseResponse> {

    @Override
    void prepare(AbstractKeyboardMessage request) {
        log.debug 'Removing previous message with reply keyboard'
        messageService.removeMessagesOfTypeExcludingUpdateId(MessageFeature.REPLY_KEYBOARD, updateId)
        super.prepare(request)
    }

    private static int getUpdateId() {
        def updateId = SessionUtil.getStringAttribute(UPDATE_ID)
        updateId ? Integer.parseInt(updateId) : -1
    }
}
