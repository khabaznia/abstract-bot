package com.khabaznia.bots.core.model

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@ToString
@Entity(name = "config")
class Config {

    @Id
    @NotNull
    @Column(name = "key")
    String key

    @NotNull
    @Column(name = "value")
    String value

    @Column(name = "name")
    String name
}
