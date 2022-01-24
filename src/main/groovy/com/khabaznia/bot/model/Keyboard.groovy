package com.khabaznia.bot.model

import com.khabaznia.bot.enums.KeyboardType
import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapKeyColumn
import javax.persistence.OneToMany

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
