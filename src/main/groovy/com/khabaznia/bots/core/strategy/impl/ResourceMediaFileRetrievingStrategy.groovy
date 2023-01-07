package com.khabaznia.bots.core.strategy.impl

import com.khabaznia.bots.core.strategy.MediaFileRetrievingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@Component(value = 'resourceMediaFileRetrievingStrategy')
class ResourceMediaFileRetrievingStrategy implements MediaFileRetrievingStrategy {

    private static final String MEDIA_FOLDER = 'media'

    @Override
    InputStream getMediaForCode(String fileCode) {
        log.debug 'Retrieving file with name {} in internal folder', fileCode
        new File("src/main/resources/$MEDIA_FOLDER/$fileCode").newInputStream()
    }
}
