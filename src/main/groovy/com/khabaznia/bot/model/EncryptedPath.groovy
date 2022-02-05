package com.khabaznia.bot.model

import groovy.transform.ToString
import org.hibernate.annotations.UpdateTimestamp

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@ToString
@Entity(name = "encrypted_path")
class EncryptedPath {

    @Id
    @NotNull
    @Column(name = 'key')
    String key

    @Column(name = 'value')
    String value

    @Column(name = "update_date")
    @UpdateTimestamp
    Date updateDate
}
