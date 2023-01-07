package com.khabaznia.bots.core.model

import groovy.transform.ToString

import javax.persistence.*

@ToString
@Entity(name = "media")
class Media {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "file_id", unique = true)
    String fileId

    @Column(name = "code", unique = true)
    String code

    @Column(name = "label", unique = true)
    String label
}
