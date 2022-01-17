package com.khabaznia.bot.repository

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.Message
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT m FROM message m JOIN m.chat c WHERE m.type=?1 AND c.code=?2 ORDER BY m.updateDate DESC ")
    @EntityGraph(attributePaths = ["type", "chatId"])
    List<Message> findByAndPaymentId(MessageType type, String chatId);
}
