package com.khabaznia.bot.model

import groovy.transform.ToString

import javax.persistence.CascadeType
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
import javax.persistence.OneToMany

@Entity(name = "row")
@ToString(excludes = 'keyboard')
class Row {

    @Id
    @Column(name = "row_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @OneToMany(mappedBy = "row", cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE], fetch = FetchType.EAGER)
    @Column(name = "row_buttons")
    List<Button> buttons

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyboard_id")
    Keyboard keyboard
}
