package com.khabaznia.bot.model

import com.khabaznia.bot.enums.KeyboardType
import groovy.transform.ToString

import javax.persistence.*

@ToString
@Entity(name = "keyboard")
class Keyboard {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    KeyboardType type

    @OneToMany(mappedBy = "keyboard", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @Column(name = "buttons")
    List<Button> buttons
}
