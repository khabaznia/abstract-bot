package com.khabaznia.bot.cronjob

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.service.PathCryptService
import com.khabaznia.bot.trait.Logged
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class CleanDatabaseCronJob {

    @Autowired
    private PathCryptService pathCryptService
    @Autowired
    private MessageService messageService
    @Autowired
    private ApplicationEventPublisher publisher

    @Scheduled(cron = '0 0 12 * * *') // Every day at noon
    @Async
    void scheduleTaskUsingCronExpression() {
        log.info '***********************************************************************************'
        log.info '***********************************************************************************'
        log.info '************* Running cronjob for deleting expired messages and paths *************'
        log.info '***********************************************************************************'
        log.info '***********************************************************************************'
        def deletedPaths = pathCryptService.deleteExpiredPaths()
        def deletedMessages = messageService.deleteExpiredMessages()
        def deletedKeyboards = messageService.deleteOrphanedKeyboards()
        publisher.publishEvent(new LogEvent(logChat: LoggingChat.ADMIN, skipMetaInfo: true,
                text: "CleanUpJob || Deleted: \n$deletedPaths paths\n$deletedMessages messages\n$deletedKeyboards orhaned keyboards"))
        log.info '***********************************************************************************'
        log.info '***********************************************************************************'
        log.info '*******************************       FINISHED       ******************************'
        log.info '***********************************************************************************'
        log.info '***********************************************************************************'
    }
}
