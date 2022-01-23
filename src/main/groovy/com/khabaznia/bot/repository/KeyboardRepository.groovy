package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Keyboard
import org.springframework.data.jpa.repository.JpaRepository

interface KeyboardRepository extends JpaRepository<Keyboard, Long> {
}