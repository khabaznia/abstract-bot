package com.khabaznia.bots.core.repository

import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends JpaRepository<User, String> {

    List<User> findByRole(UserRole role)

    @Query(value = "SELECT u FROM bot_user u LEFT JOIN u.subscription s WHERE s.general=true")
    List<User> getAllByGeneralSubscription()
}
