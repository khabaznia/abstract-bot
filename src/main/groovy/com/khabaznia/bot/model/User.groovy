package com.khabaznia.bot.model

import com.khabaznia.bot.enums.UserRole
import groovy.transform.ToString

import javax.persistence.*

@Entity(name = "bot_user")
@ToString(excludes = 'chat')
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
