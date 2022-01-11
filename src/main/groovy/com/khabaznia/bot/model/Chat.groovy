package com.khabaznia.bot.model

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.enums.ChatType

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "chat")
class Chat {

    @Id
    @NotNull
    @Column(name = "chat_id")
    String code

    @Column(name = "lang")
    String lang

    @Column(name = "last_action")
    String lastAction

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "type")
    ChatType type

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role")
    ChatRole role

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @Column(name = "history")
    List<Message> history

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @Column(name = "users")
    List<User> users
}
