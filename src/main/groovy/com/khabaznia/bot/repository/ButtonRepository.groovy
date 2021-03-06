package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Button
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ButtonRepository extends JpaRepository<Button, String> {
}