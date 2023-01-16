package com.khabaznia.bots.core.model

import groovy.transform.ToString

import javax.persistence.*

@Entity(name = 'edit_flow')
@ToString
class EditFlow {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column
    Long entityId
    @Column
    String entityClassName
    @Column
    String fieldName
    @Column
    String lang

    @Column
    String successPath
    @Column
    String successMessage

    @Column
    String enterText
    @Column
    String oldValue

    @ElementCollection
    @CollectionTable(name = 'edit_flow_enter_text_binding',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'key')
    @Column(name = 'value', columnDefinition = "TEXT")
    Map<String, String> enterTextBinding

    @ElementCollection
    @CollectionTable(name = 'edit_flow_params',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'key')
    @Column(name = 'value', columnDefinition = "TEXT")
    Map<String, String> params
}
