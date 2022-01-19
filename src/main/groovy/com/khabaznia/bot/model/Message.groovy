package com.khabaznia.bot.model

import com.khabaznia.bot.enums.MessageType
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "message")
@ToString(excludes = 'chat')
class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code")
    Long code

    @NotNull
    @Column(name = "message_id")
    Integer messageId

    @NotNull
    @Column(name = "text")
    String text

    @Column(name = "update_date")
    @UpdateTimestamp
    Date updateDate

    @Enumerated
    @Column(name = "type")
    MessageType type

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    Chat chat

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "keyboard_id")
    Keyboard keyboard
}
