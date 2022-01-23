package com.khabaznia.bot.strategy

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
abstract class RequestProcessingStrategy<Request extends BaseRequest, Response extends BaseResponse> {

    @Autowired
    ApiMethodService apiMethodService
    @Autowired
    MessageService messageService

    Message beforeProcess(Request request) {
        log.debug 'Saving message before sending api request'
        Message message = getMessageFromRequest(request)
        log.trace(message as String)
        def result = messageService.saveMessage(message)
        log.trace 'Saved message -> {}', result
        if (request instanceof AbstractKeyboardMessage) {
            def keyboard = messageService.saveKeyboard(toKeyboardModel(request.keyboard))
            message.setKeyboard(keyboard)
            result = messageService.saveMessage(message)
            log.trace 'Saved message with keyboard - > {}', result
        }
        result
    }

    Response process(Request request) {
        apiMethodService.execute(request) as Response
    }

    void afterProcess(Response response, Message message) {
        if (response instanceof MessageResponse) {
            message.setMessageId(response.result.messageId)
            message.setText(response.result.text)
            log.debug 'Saving message with messageId: {}', response.result.messageId
            messageService.saveMessage(message)
        }
    }

    protected Message getMessageFromRequest(BaseRequest request) {
        new Message(type: request.type,
                messageId: 1,
                chat: SessionUtil.currentChat)
    }
}
