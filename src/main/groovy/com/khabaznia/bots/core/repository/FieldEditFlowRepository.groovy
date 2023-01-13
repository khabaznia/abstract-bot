package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.model.FieldEditFlow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FieldEditFlowRepository extends JpaRepository<FieldEditFlow, Long> {
}