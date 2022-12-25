package com.khabaznia.bot.scheduled

import com.khabaznia.bot.enums.JobStatus
import com.khabaznia.bot.event.ExecuteMethodsEvent
import com.khabaznia.bot.exception.BotServiceException
import com.khabaznia.bot.meta.container.PrototypeRequestsContainer
import com.khabaznia.bot.service.BotMessagesService
import com.khabaznia.bot.service.JobService
import com.khabaznia.bot.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

@Slf4j
abstract class AbstractJob implements Runnable, Loggable {

    public Long modelId
    boolean deleteJobOnFinish = false

    @Autowired
    protected PrototypeRequestsContainer requestsContainer
    @Autowired
    protected ApplicationEventPublisher publisher
    @Autowired
    protected BotMessagesService botMessagesService
    @Autowired
    protected JobService jobService

    @Override
    void run() {
        log()
        if (!jobService.isExists(modelId))
            return
        jobService.updateJobStatus(modelId, JobStatus.IN_PROGRESS)
        try {
            executeInternal()
            jobService.updateJobStatus(modelId, JobStatus.FINISHED)
        } catch (BotServiceException ex) {
            jobService.updateJobStatus(modelId, JobStatus.FINISHED)
            questLog(ex)
        } catch (Exception ignored) {
            jobService.updateJobStatus(modelId, JobStatus.FAILED)
        } finally {
            if (deleteJobOnFinish)
                jobService.deleteJob(modelId)
        }
    }

    protected void executeInternal() {
        publisher.publishEvent new ExecuteMethodsEvent(requests: requestsContainer.convertedRequests())
    }

    protected abstract String getJobTitle()

    protected void log() {
        log.debug '{} ({})', getJobTitle(), modelId
    }

    protected questLog(BotServiceException ex) {
        sendLog(ex.message, ex.binding)
    }
}
