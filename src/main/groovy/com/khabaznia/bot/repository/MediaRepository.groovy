package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Media
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaRepository extends JpaRepository<Media, Long> {

    Media findByCode(String code)

    Media findByFileId(String fileId)

    Media findByLabel(String label)

    boolean existsByFileId(String fileId)
}