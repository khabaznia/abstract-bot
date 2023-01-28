package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
import com.khabaznia.bots.core.util.BotSession
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.CHAT_PARAMS.CURRENT_REPLY_KEYBOARD_ID
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

    @Override
    void processResponse(BaseResponse response) {
        def message = messageService.getMessage(response.relatedMessageUid)
        chatService.setChatParam(CURRENT_REPLY_KEYBOARD_ID, message.keyboard?.id?.toString(), message.chat.code)
        super.processResponse(response)
    }

    private static int getUpdateId() {
        def updateId = BotSession.getStringAttribute(UPDATE_ID)
        updateId ? Integer.parseInt(updateId) : -1
    }
}
