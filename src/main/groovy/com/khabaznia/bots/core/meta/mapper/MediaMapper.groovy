package com.khabaznia.bots.core.meta.mapper

import com.khabaznia.bots.core.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bots.core.service.MediaService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.InputFile

import javax.annotation.Resource


@Slf4j
@Component
class MediaMapper {

    @Resource(name = 'resourceMediaService')
    private MediaService resourceMediaService

    InputFile toApiInputFile(String fileIdentifier, boolean forceNew = false, MediaService mediaService = resourceMediaService) {
        if (forceNew)
            return loadNewFile(fileIdentifier, mediaService)

        def media = mediaService.getMedia(fileIdentifier)
        if (media && media.fileId) {
            log.trace 'Found cached media in db. Sending by direct file id'
            return new InputFile(media.fileId)
        } else {
            if (fileIdentifier.size() > 60) {
                log.trace 'Seems it unique file id of telegram media. Sending by direct file id'
                return new InputFile(fileIdentifier)
            }
            log.trace 'Media in db is not found. Resolving new file by strategy'
            return loadNewFile(fileIdentifier, mediaService)
        }
    }

    InputFile toApiInputFile(AbstractMediaRequest request) {
        toApiInputFile(request.fileIdentifier, false, resourceMediaService)
    }

    private static InputFile loadNewFile(String fileId, MediaService mediaService) {
        new InputFile(mediaService.getFileInputStreamForMediaCode(fileId), fileId)
    }
}
