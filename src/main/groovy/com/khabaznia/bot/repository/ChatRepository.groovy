package com.khabaznia.bot.repository

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository extends JpaRepository<Chat, String> {

    List<Chat> findByRole(ChatRole role)

    Chat getByCode(String code)

    boolean existsByCode(String code)
}
