package com.khabaznia.bot.model

import com.khabaznia.bot.enums.UserRole
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "bot_user")
@ToString(excludes='chat')
class User {

    @Id
    @Column(name = "user_id")
    String code

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    UserRole role

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    Chat chat
}
