package com.khabaznia.bots.core.model

import com.khabaznia.bots.core.enums.ButtonType
import groovy.transform.ToString

import javax.persistence.*

@Entity(name = "button")
@ToString(excludes = 'keyboard')
class Button {

    @Id
    @Column(name = "id")
    String id

    @Column(name = "key")
    String key

    @Column(name = "emoji")
    String emoji

    @Column(name = "callback_data")
    String callbackData

    @Column(name = "button_position")
    Integer position

    @Column(name = "row_position")
    Integer rowPosition

    @Column(name = "url")
    String url

    @ElementCollection
    @CollectionTable(name = "binding_mapping",
            joinColumns = [@JoinColumn(name = "id", referencedColumnName = "id")])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    Map<String, String> binding

    @ElementCollection
    @CollectionTable(name = "params_mapping",
            joinColumns = [@JoinColumn(name = "id", referencedColumnName = "id")])
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    Map<String, String> params

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    ButtonType type

    @ManyToOne
    @JoinColumn(name = "keyboard_id")
    Keyboard keyboard
}
