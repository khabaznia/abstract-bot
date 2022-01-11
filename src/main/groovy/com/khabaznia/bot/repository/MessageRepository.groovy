package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository extends JpaRepository<Message, Long> {
}
