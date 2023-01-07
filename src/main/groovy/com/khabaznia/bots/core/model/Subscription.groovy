package com.khabaznia.bots.core.model


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

@ToString()
@EqualsAndHashCode()
@Entity(name = 'subscription')
class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id')
    Long id

    @Column(name = 'general')
    boolean general
}
