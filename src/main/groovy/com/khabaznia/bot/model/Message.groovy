package com.khabaznia.bot.model

import com.khabaznia.bot.enums.MessageType
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity(name = "message")
@ToString(excludes = 'chat')
class Message {

    @Id
    @Column(name = "uid")
    String uid

    @Column(name = "message_id")
    Integer messageId

    @Column(name = "text")
    String text

    @Column(name = "label")
    String label

    @Column(name = "update_date")
    @UpdateTimestamp
    Date updateDate

    @Column(name = "isSent")
    Boolean isSent

    @Enumerated(value = EnumType.STRING)
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
