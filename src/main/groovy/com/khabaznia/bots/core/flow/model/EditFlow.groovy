package com.khabaznia.bots.core.flow.model

import com.khabaznia.bots.core.flow.enums.FieldType
import groovy.transform.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

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

    @Column
    @Enumerated(EnumType.STRING)
    FieldType type

    @Column
    String oldValue
    @ElementCollection
    @Column(name = "selected_ids")
    List<Long> selectedIds = []
    @ElementCollection
    @Column(name = "initialIds")
    List<Long> initialIds = []
    @Column
    String entityFactory

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = 'child_edit_flow')
    EditFlow childFlow
}
