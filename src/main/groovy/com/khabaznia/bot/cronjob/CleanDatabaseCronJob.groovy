package com.khabaznia.bot.cronjob

import com.khabaznia.bot.service.MessageService
import com.khabaznia.bot.service.PathCryptService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class CleanDatabaseCronJob {

    @Autowired
    PathCryptService pathCryptService
    @Autowired
    MessageService messageService

    @Scheduled(cron = '0 0 12 * * TUE') // Every Tuesday at noon
    @Async
    public void scheduleTaskUsingCronExpression() {
        log.info '************* Running cronjob for deleting expired messages and paths *************'
        pathCryptService.deleteExpiredPaths()
        messageService.deleteExpiredMessages()
        log.info '******************************       FINISHED       ******************************'
    }
}
