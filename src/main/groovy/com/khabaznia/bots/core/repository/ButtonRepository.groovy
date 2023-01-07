package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.model.Button
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ButtonRepository extends JpaRepository<Button, String> {
}