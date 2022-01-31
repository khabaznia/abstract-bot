package com.khabaznia.bot.strategy

import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
abstract class RequestProcessingStrategy<Request extends BaseRequest, Response extends BaseResponse> {

    @Autowired
    protected MessageService messageService
    @Autowired
    protected RequestMapper requestMapper

    void prepare(Request request) {
        log.debug 'Saving message for request {} before sending api request. Type: {}', request?.class?.simpleName, request?.type
        Message message = getMessageFromRequest(request)
        messageService.saveMessage(message)
    }

    void updateWithMappedApiMethod(Request request) {
        request.setApiMethod(requestMapper.toApiMethod(request))
    }

    void processResponse(Response response) {
        def message = messageService.getMessage(response.relatedMessageUid)
        if (response && message && response instanceof MessageResponse) {
            message.setMessageId(response.result.messageId)
            message.setText(response.result.text)
            log.debug 'Saving message with messageId: {}', response.result.messageId
            messageService.saveMessage(message)
        }
    }

    protected static Message getMessageFromRequest(BaseRequest request) {
        def label = request instanceof SendMessage ? request.label : null
        def uid = UUID.randomUUID().toString()
        def keyboard = request instanceof AbstractKeyboardMessage ? toKeyboardModel(request.keyboard) : null
        request.setRelatedMessageUid(uid)
        new Message(uid: uid,
                type: request.type,
                chat: SessionUtil.currentChat,
                label: label,
                keyboard: keyboard)
    }
}
