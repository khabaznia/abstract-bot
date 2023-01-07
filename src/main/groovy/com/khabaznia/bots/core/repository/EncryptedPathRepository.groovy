package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.model.EncryptedPath
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EncryptedPathRepository extends JpaRepository<EncryptedPath, String> {

    @Query("SELECT p FROM encrypted_path p WHERE p.updateDate <= :updateTimeStamp")
    List<EncryptedPath> findAllWithUpdateDateTimeBefore(@Param("updateTimeStamp") Date updateTimeStamp)

    List<EncryptedPath> findByValueContaining(String messageId)
}