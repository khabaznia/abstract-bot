package com.khabaznia.bot.cronjob

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.sender.BotRequestQueueContainer
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

        BotRequestQueueContainer.requestOrder.set(0)
        def deletedPaths = pathCryptService.deleteExpiredPaths()
        def deletedMessages = messageService.deleteExpiredMessages()
        def deletedKeyboards = messageService.deleteOrphanedKeyboards()
        log.info '{}', logMessage(deletedPaths, deletedMessages, deletedKeyboards)
        publisher.publishEvent(new LogEvent(logChat: LoggingChat.ADMIN, skipMetaInfo: true,
                text: logMessage(deletedPaths, deletedMessages, deletedKeyboards)))


        log.info '***********************************************************************************'
        log.info '*******************************       FINISHED       ******************************'
        log.info '***********************************************************************************'
    }

    private static String logMessage(int deletedPaths, int deletedMessages, int deletedKeyboards) {
        "${TRIANGLE_RIGHT}Clean-up job${TRIANGLE_LEFT} \n$deletedPaths paths\n$deletedMessages messages\n$deletedKeyboards orhaned keyboards"
    }
}
