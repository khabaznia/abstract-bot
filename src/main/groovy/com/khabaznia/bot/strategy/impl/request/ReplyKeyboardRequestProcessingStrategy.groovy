package com.khabaznia.bot.strategy.impl.request

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID


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
