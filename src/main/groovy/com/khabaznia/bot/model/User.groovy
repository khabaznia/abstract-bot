package com.khabaznia.bot.model

import com.khabaznia.bot.enums.UserRole
import groovy.transform.ToString

import javax.persistence.*

@Entity(name = "bot_user")
@ToString(excludes = 'chats')
class User {

    @Id
    @Column(name = "user_id")
    String code

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    UserRole role

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = 'users')
    List<Chat> chats
}
