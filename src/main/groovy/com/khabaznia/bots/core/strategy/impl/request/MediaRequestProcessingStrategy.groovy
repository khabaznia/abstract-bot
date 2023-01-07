package com.khabaznia.bots.core.strategy.impl.request

import com.khabaznia.bots.core.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import com.khabaznia.bots.core.service.MediaService
import com.khabaznia.bots.core.strategy.RequestProcessingStrategy
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
