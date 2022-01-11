package com.khabaznia.bot.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "config")
class Config {

    @Id
    @NotNull
    @Column(name = "key")
    String key

    @NotNull
    @Column(name = "value")
    String value

    @Column(name = "is_switchable")
    Boolean isSwitchable

    @Column(name = "name")
    String name
}
