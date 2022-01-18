package com.khabaznia.bot.model

import groovy.transform.ToString

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn

@Entity(name = "row")
@ToString(excludes = 'keyboard')
class Row {

    @Id
    @Column(name = "row_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ElementCollection
    @CollectionTable(name = "button_mapping",
            joinColumns = [@JoinColumn(name = "row_id", referencedColumnName = "row_id")])
    @MapKeyColumn(name = "callback_query")
    @Column(name="text")
    Map<String, String> buttons

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyboard_id")
    Keyboard keyboard
}
