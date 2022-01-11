package com.khabaznia.bot.repository

import com.khabaznia.bot.model.Config
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepository extends JpaRepository<Config, String>{
}
