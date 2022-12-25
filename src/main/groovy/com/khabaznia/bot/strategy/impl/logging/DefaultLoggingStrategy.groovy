package com.khabaznia.bot.strategy.impl.logging

import com.khabaznia.bot.strategy.LoggingStrategy
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.LOG_INFO

@Slf4j
@Component(value = 'defaultLoggingStrategy')
class DefaultLoggingStrategy extends LoggingStrategy {

    @Override
    String getLogEmoji() {
        LOG_INFO
    }
}
