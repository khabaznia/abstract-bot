package com.khabaznia.bot.strategy.impl.request

import com.khabaznia.bot.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bot.meta.response.impl.MessageResponse
import com.khabaznia.bot.service.MediaService
import com.khabaznia.bot.strategy.RequestProcessingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Slf4j
@Component(value = 'mediaRequestProcessingStrategy')
class MediaRequestProcessingStrategy extends RequestProcessingStrategy<AbstractMediaRequest, MessageResponse> {

    @Resource(name = 'resourceMediaService')
    private MediaService mediaService

    @Override
    void prepare(AbstractMediaRequest request) {
        def message = getMessageFromRequest(request)
        def savedMedia = mediaService.saveMedia(request.fileIdentifier, request.label)
        message.setRelatedMediaId(savedMedia.id)
        messageService.saveMessage(message)
    }

    @Override
    void processResponse(MessageResponse response) {
        if (response.relatedMessageUid) {
            def message = messageService.getMessage(response.relatedMessageUid)
            def relatedMedia = mediaService.getMedia(message?.relatedMediaId?.toString())
            log.debug 'Processing message response after sending media'
            def responseMedia = response?.result?.media
            relatedMedia?.setFileId(responseMedia?.fileId)
            mediaService.saveMedia(relatedMedia)
        }
        super.processResponse(response)
    }
}
