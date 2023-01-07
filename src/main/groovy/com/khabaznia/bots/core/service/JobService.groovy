package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.JobStatus
import com.khabaznia.bots.core.model.Job
import com.khabaznia.bots.core.repository.JobRepository
import com.khabaznia.bots.core.scheduled.AbstractJob
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.format.DateTimeFormatter

@Slf4j
@Service
class JobService {

    @Autowired
    private JobRepository repository
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler
    @Autowired
    private ApplicationContext context

    void scheduleJob(AbstractJob job, Date time) {
        job.modelId = repository.save(convertToModel(job, time)).id
        log.trace 'Scheduling job {} to time {}. Id: {}', job.class.name, stringTime(time), job.modelId
        taskScheduler.schedule(job, time)
    }

    void restoreJobs() {
        (repository.findAllByStatus(JobStatus.IN_PROGRESS) + repository.findAllByStatus(JobStatus.SCHEDULED))
                .each {
                    log.debug 'Restoring job {}. Status {}.Scheduled time: {}', it.id, it.status, stringTime(it.scheduledTime)
                    taskScheduler.schedule(convertToExecutable(it), it.scheduledTime)
                }
    }

    void updateJobStatus(Long jobId, JobStatus jobStatus) {
        def job = repository.getById(jobId)
        job.status = jobStatus
        repository.save(job)
        log.trace 'Job {} status updated - {}', jobId, jobStatus
    }

    boolean isExists(Long jobId) {
        repository.existsById(jobId)
    }

    void deleteJob(Long jobId) {
        repository.deleteById(jobId)
    }

    private static String stringTime(Date time) {
        time.toLocalDateTime().format(DateTimeFormatter.ISO_TIME)
    }

    private AbstractJob convertToExecutable(Job job) {
        def abstractJob = context.getBean(job.beanId) as AbstractJob
        abstractJob.modelId = job.id
        populateParams(job.params, abstractJob)
        abstractJob
    }

    private static void populateParams(Map<String, String> params, AbstractJob job) {
        def fields = getJobDataFields(job).collectEntries { [(it.name): it.type] }
        params.findAll { fields.containsKey(it.key) }
                .each { job.setProperty(it.key, fields.get(it.key).'valueOf'(it.value)) }
    }

    private Job convertToModel(AbstractJob job, Date scheduledTime) {
        new Job(status: JobStatus.SCHEDULED,
                params: getParams(job),
                beanId: context.getBeanNamesForType(job.class)[0],
                scheduledTime: scheduledTime)
    }

    private static Map<String, String> getParams(AbstractJob job) {
        getJobDataFields(job)
                .findAll { job."$it.name" != null }
                .collectEntries { field -> [field.name, job."$field.name".toString()] }
    }

    private static Collection<Field> getJobDataFields(AbstractJob job) {
        (job.getClass().declaredFields + job.getClass().getSuperclass().declaredFields)
                .findAll { !it.synthetic }
                .findAll { !it.annotations.contains(Autowired.class) }
                .findAll { it.name != "modelId" }
                .findAll { Modifier.isPublic(it.modifiers) }
    }
}
