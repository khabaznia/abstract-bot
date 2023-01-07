package com.khabaznia.bots.core.configuration

import com.khabaznia.bots.core.repository.MediaRepository
import com.khabaznia.bots.core.service.MediaService
import com.khabaznia.bots.core.strategy.impl.ResourceMediaFileRetrievingStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeansConfig {

    @Autowired
    private ResourceMediaFileRetrievingStrategy mediaFileRetrievingStrategy
    @Autowired
    private MediaRepository mediaRepository

    @Bean(name = 'resourceMediaService')
    MediaService mediaService() {
        new MediaService(
                mediaFileRetrievingStrategy: mediaFileRetrievingStrategy,
                mediaRepository: mediaRepository)
    }
}
