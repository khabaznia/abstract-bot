package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.model.Config
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepository extends JpaRepository<Config, String> {

    List<Config> findByKeyStartsWith(String prefix)
}
