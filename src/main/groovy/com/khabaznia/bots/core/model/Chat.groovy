package com.khabaznia.bots.core.model

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.enums.ChatType
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import javax.persistence.*
import javax.validation.constraints.NotNull

@ToString(excludes = 'history')
@Entity(name = "chat")
class Chat {

    @Id
    @NotNull
    @Column(name = "chat_id")
    String code

    @Column(name = "title")
    String title

    @Column(name = "lang")
    String lang

    @Column(name = "last_action")
    String lastAction

    @Column(name = "last_action_full_path", columnDefinition = "TEXT")
    String lastActionFullPath

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    ChatType type

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role")
    ChatRole role

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @Column(name = "history")
    List<Message> history

    @ManyToMany
    @JoinTable(
            name = "chat_user",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<User> users

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = 'permissions')
    Permissions permissions

    @ElementCollection
    @CollectionTable(name = "additional_params_mapping",
            joinColumns = [@JoinColumn(name = "id", referencedColumnName = "chat_id")])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    Map<String, String> additionalParams

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = 'edit_flow')
    EditFlow editFlow
}
