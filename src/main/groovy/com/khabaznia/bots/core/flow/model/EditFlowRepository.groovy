package com.khabaznia.bots.core.flow.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EditFlowRepository extends JpaRepository<EditFlow, Long> {
}