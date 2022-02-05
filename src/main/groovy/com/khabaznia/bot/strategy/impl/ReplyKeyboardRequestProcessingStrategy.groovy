package com.khabaznia.bot.strategy.impl

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_ID_ATTR


@Slf4j
@Component(value = 'replyKeyboardRequestProcessingStrategy')
class ReplyKeyboardRequestProcessingStrategy extends RequestProcessingStrategy<AbstractKeyboardMessage, BaseResponse> {

    @Autowired
    private ApplicationEventPublisher publisher

    @Override
    void prepare(AbstractKeyboardMessage request) {
        log.debug 'Removing previous message with reply keyboard'
        messageService.removeMessagesOfTypeExcludingUpdateId(MessageType.REPLY_KEYBOARD,
                Integer.parseInt(SessionUtil.getAttribute(UPDATE_ID_ATTR)))
        super.prepare(request)
    }
}
