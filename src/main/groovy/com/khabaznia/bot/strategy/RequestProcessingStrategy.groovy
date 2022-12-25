package com.khabaznia.bot.strategy

import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.service.ChatService
import com.khabaznia.bot.service.MessageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bot.meta.mapper.KeyboardMapper.toKeyboardModel

@Slf4j
abstract class RequestProcessingStrategy<Request extends BaseRequest, Response extends BaseResponse> {

    @Autowired
    protected MessageService messageService
    @Autowired
    protected RequestMapper requestMapper
    @Autowired
    protected ChatService chatService

    void prepare(Request request) {
        log.debug 'Saving message for request {} before sending api request. Class: {}', request?.class?.simpleName, request?.features
        def message = getMessageFromRequest(request)
        messageService.saveMessage(message)
    }

    void processResponse(Response response) {
        if (response && response.relatedMessageUid) {
            def message = messageService.getMessage(response.relatedMessageUid)
            if (message && response instanceof MessageResponse) {
                message.setMessageId(response.result.messageId)
                message.setText(response.result.text)
                log.debug 'Saving message with messageId: {}', response.result.messageId
                messageService.saveMessage(message)
            }
        }
    }

    protected Message getMessageFromRequest(BaseRequest request) {
        request.relatedMessageUid
                ? messageService.getMessage(request.relatedMessageUid) ?: createNewMessage(request)
                : createNewMessage(request)
    }

    private Message createNewMessage(BaseRequest request) {
        def label = request instanceof SendMessage ? request.label :
                request instanceof AbstractMediaRequest ? request.messageLabel : null
        def uid = UUID.randomUUID().toString()
        def keyboard = request instanceof AbstractKeyboardMessage ? toKeyboardModel(request.keyboard) : null
        request.setRelatedMessageUid(uid)
        new Message(uid: uid,
                features: request.features,
                chat: chatService.getChatByCode(request.chatId),
                label: label,
                keyboard: keyboard,
                updateId: request.updateId)
    }
}
