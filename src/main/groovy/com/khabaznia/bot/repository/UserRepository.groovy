package com.khabaznia.bot.repository

import com.khabaznia.bot.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends JpaRepository<User, String> {
}
