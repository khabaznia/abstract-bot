package com.khabaznia.bots.core.model

import groovy.transform.ToString

import javax.persistence.*

@Entity(name = 'edit_flow')
@ToString
class FieldEditFlow {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column
    Long entityId
    @Column
    String fieldName
    @Column
    Boolean localized
    @Column
    String lang
    @Column
    String successPath

    @Column
    String repoBeanId
    @Column
    String validationMethod
    @Column
    String enterMessage
    @Column
    String successMessage
}
