package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Keyboard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface KeyboardRepository extends JpaRepository<Keyboard, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM keyboard k WHERE k.id NOT IN (SELECT m.keyboard_id FROM message m)")
    List<Keyboard> findAllOrphaned()
}