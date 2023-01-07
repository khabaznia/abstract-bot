package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.enums.JobStatus
import com.khabaznia.bots.core.model.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByStatus(JobStatus status)
}