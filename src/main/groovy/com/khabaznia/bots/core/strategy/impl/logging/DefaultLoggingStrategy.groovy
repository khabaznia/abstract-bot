package com.khabaznia.bots.core.strategy.impl.logging

import com.khabaznia.bots.core.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.meta.Emoji.LOG_INFO

@Slf4j
@Component(value = 'defaultLoggingStrategy')
class DefaultLoggingStrategy extends LoggingStrategy {

    @Override
    String getLogEmoji() {
        LOG_INFO
    }
}
