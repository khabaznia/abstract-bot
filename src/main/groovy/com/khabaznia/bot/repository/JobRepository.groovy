package com.khabaznia.bot.repository

import com.khabaznia.bot.enums.JobStatus
import com.khabaznia.bot.model.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByStatus(JobStatus status)
}