package com.khabaznia.bot.repository

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.model.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value = "SELECT m FROM message m LEFT JOIN m.chat c  WHERE :type MEMBER OF m.features AND m.messageId IS NOT NULL AND c.code=:chatCode AND m.updateId<>:updateId")
    List<Message> findByTypeAndChatCodeThatNotOfUpdateId(@Param("type") MessageFeature type, @Param("chatCode") String chatCode, @Param("updateId") Integer updateId)

    @Query("SELECT m FROM message m where m.updateDate <= :updateTimeStamp")
    List<Message> findAllWithUpdateDateTimeBefore(@Param("updateTimeStamp") Date updateTimeStamp)

    @Query(value = "SELECT m FROM message m LEFT JOIN m.chat c WHERE c.code=:chatCode AND m.updateId=:updateId")
    List<Message> findAllUnsentByChatCode(@Param("chatCode") String chatCode, @Param("updateId") Integer updateId)

    Message findByLabel(String label)

    Message findByMessageId(Integer messageId)

    boolean existsByLabel(String label)
}
