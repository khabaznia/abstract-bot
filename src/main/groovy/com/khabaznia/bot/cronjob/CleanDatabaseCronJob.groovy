package com.khabaznia.bot.cronjob

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.service.PathCryptService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import static com.khabaznia.bot.meta.Emoji.TRIANGLE_LEFT
import static com.khabaznia.bot.meta.Emoji.TRIANGLE_RIGHT

@Slf4j
@Component
class CleanDatabaseCronJob {

    @Autowired
    private PathCryptService pathCryptService
    @Autowired
    private MessageService messageService
    @Autowired
    private ApplicationEventPublisher publisher

    @Scheduled(cron = '${clean.up.database.cron.expression}')
    @Async
    void cleanUpTask() {
        log.info '***********************************************************************************'
        log.info '************* Running cronjob for deleting expired messages and paths *************'
        log.info '***********************************************************************************'


        def deletedPaths = pathCryptService.deleteExpiredPaths()
        def deletedMessages = messageService.deleteExpiredMessages()
        def deletedKeyboards = messageService.deleteOrphanedKeyboards()
        publisher.publishEvent(new LogEvent(logChat: LoggingChat.ADMIN, skipMetaInfo: true,
                text: "${TRIANGLE_RIGHT}Clean-up job${TRIANGLE_LEFT} \n$deletedPaths paths\n$deletedMessages messages\n$deletedKeyboards orhaned keyboards"))


        log.info '***********************************************************************************'
        log.info '*******************************       FINISHED       ******************************'
        log.info '***********************************************************************************'
    }
}
