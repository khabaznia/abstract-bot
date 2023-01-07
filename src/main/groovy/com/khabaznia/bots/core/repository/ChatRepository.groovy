package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository extends JpaRepository<Chat, String> {

    List<Chat> findByRole(ChatRole role)

    Chat getByCode(String code)

    boolean existsByCode(String code)
}
