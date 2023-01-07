package com.khabaznia.bots.core.model

import com.khabaznia.bots.core.enums.JobStatus
import groovy.transform.ToString

import javax.persistence.*

@ToString
@Entity(name = 'job')
class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id')
    Long id

    @Column(name = 'bean_id')
    String beanId

    @ElementCollection
    @CollectionTable(name = 'job_params',
            joinColumns = [@JoinColumn(name = 'job_id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'key')
    @Column(name = 'value')
    Map<String, String> params

    @Column(name = 'scheduled_time')
    Date scheduledTime

    @Column(name = 'status')
    @Enumerated(value = EnumType.STRING)
    JobStatus status
}
