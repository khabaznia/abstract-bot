package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Row
import org.springframework.data.jpa.repository.JpaRepository

interface RowRepository extends JpaRepository<Row, Long> {
}