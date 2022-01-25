package com.khabaznia.bot.repository

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.EncryptedPath
import com.khabaznia.bot.model.Message
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT m FROM message m LEFT JOIN m.chat c WHERE m.type=?1 AND c.code=?2 ORDER BY m.updateDate DESC ")
    List<Message> findByTypeAndChatCode(MessageType type, String chatCode)

    @Query("SELECT m FROM message m where m.updateDate >= :updateTimeStamp")
    List<Message> findAllWithUpdateDateTimeAfter(@Param("updateTimeStamp") Date updateTimeStamp)

    Message findByLabel(String label)

    Message findByMessageId(Integer messageId)

    boolean existsByLabel(String label)
}
