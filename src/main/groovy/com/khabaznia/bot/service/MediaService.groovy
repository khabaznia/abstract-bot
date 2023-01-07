package com.khabaznia.bot.service

import com.khabaznia.bot.model.Media
import com.khabaznia.bot.repository.MediaRepository
import com.khabaznia.bot.strategy.MediaFileRetrievingStrategy
import groovy.util.logging.Slf4j

@Slf4j
class MediaService {

    public static final String MEDIA_FILE_PREFIX = 'media_'

    MediaRepository mediaRepository
    MediaFileRetrievingStrategy mediaFileRetrievingStrategy

    Media getMedia(String identifier) {
        log.debug 'Try to get media for identifier: {}', identifier
        def media = identifier
                ? (mediaRepository.existsByFileId(identifier)
                    ? mediaRepository.findByFileId(identifier)
                    : identifier.isNumber()
                        ? mediaRepository.getById(Long.valueOf(identifier))
                        : mediaRepository.findByCode(identifier))
                : null
        logMedia(media)
        media
    }

    Media getMediaForLabel(String label) {
        log.debug 'Try to resolve media by label {}', label
        label ? mediaRepository.findByLabel(label) : null
    }

    InputStream getFileInputStreamForMediaCode(String fileIdentifier) {
        log.debug 'Try to resolve media with strategy'
        mediaFileRetrievingStrategy.getMediaForCode(fileIdentifier)
    }

    Media saveMedia(String id, String label) {
        def media = getMediaForLabel(label)
                ?: getMedia(id)
                ?: new Media(code: id, label: label)
        if (label) media.setLabel(label)
        mediaRepository.save(media)
    }

    Media saveMedia(Media media) {
        log.trace 'Saving media with fileId {}', media.fileId
        mediaRepository.save(media)
    }

    private static void logMedia(Media media) {
        if (media)
            log.info 'Media found: fileId {}, code {}', media.fileId, media.code
        else
            log.info 'Media not found'
    }
}
