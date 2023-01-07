package com.khabaznia.bots.core.model

import com.khabaznia.bots.core.enums.MessageFeature
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.*

@Entity(name = "message")
@ToString(excludes = 'chat')
class Message {

    @Id
    @Column(name = "uid")
    String uid

    @Column(name = "message_id")
    Integer messageId

    @Column(name = "text", columnDefinition = "TEXT")
    String text

    @Column(name = "label")
    String label

    @Column(name = "update_date")
    @UpdateTimestamp
    Date updateDate

    @Column(name = "update_id")
    Integer updateId

    @ElementCollection
    @Enumerated(value = EnumType.STRING)
    @Column(name = "features")
    Set<MessageFeature> features

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    Chat chat

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "keyboard_id")
    Keyboard keyboard

    @Column(name = 'related_media_id')
    Long relatedMediaId
}
