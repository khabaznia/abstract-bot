package com.khabaznia.bot.repository

import com.khabaznia.bot.model.EncryptedPath
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EncryptedPathRepository extends JpaRepository<EncryptedPath, String> {

    @Query("SELECT p FROM encrypted_path p where p.updateDate >= :updateTimeStamp")
    List<EncryptedPath> findAllWithUpdateDateTimeAfter(
            @Param("updateTimeStamp") Date updateTimeStamp)

    List<EncryptedPath> findByValueContaining(String buttonOfMessageId)
}