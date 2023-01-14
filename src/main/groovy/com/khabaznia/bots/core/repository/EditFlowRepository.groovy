package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.model.EditFlow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EditFlowRepository extends JpaRepository<EditFlow, Long> {
}