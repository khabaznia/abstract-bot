package com.khabaznia.bots.core.model

import com.khabaznia.bots.core.enums.UserRole
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import javax.persistence.*

@Entity(name = "bot_user")
@ToString(excludes = 'chats')
class User {

    @Id
    @Column(name = "user_id")
    String code

    @Column(name = "first_name")
    String firstName

    @Column(name = "last_name")
    String lastName

    @Column(name = "username")
    String username

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    UserRole role

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = 'users')
    List<Chat> chats

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = 'subscription_id')
    Subscription subscription

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = 'edit_flow')
    EditFlow editFlow
}
