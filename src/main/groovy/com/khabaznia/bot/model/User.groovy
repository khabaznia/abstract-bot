package com.khabaznia.bot.model

import com.khabaznia.bot.enums.UserRole

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "bot_user")
class User {

    @Id
    @Column(name = "user_id")
    String code

    @Column(name = "role")
    UserRole role

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
