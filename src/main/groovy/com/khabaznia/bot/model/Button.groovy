package com.khabaznia.bot.model

import com.khabaznia.bot.enums.ButtonType
import groovy.transform.ToString

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn

@Entity(name = "button")
@ToString(excludes = 'row')
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id")
    Row row
}
