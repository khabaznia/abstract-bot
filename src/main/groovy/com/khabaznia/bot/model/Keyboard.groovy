package com.khabaznia.bot.model

import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@ToString
@Entity(name = "keyboard")
class Keyboard {

    @Id
    @Column(name = "keyboard_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @OneToMany(mappedBy = "keyboard", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Column(name = "rows")
    List<Row> rows
}
