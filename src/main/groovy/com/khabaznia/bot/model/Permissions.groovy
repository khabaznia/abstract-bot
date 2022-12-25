package com.khabaznia.bot.model

import groovy.transform.ToString

import javax.persistence.*

@ToString
@Entity(name = "permissions")
class Permissions {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "can_delete_messages")
    boolean canDeleteMessages

    @Column(name = "can_pin_messages")
    boolean canPinMessages

    @Column(name = "can_invite_users")
    boolean canInviteUsers

    @Column(name = "can_restrict_users")
    boolean canRestrictUsers
}
