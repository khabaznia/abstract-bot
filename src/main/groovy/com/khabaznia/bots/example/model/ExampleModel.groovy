package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern

@ToString
@Entity(name = "example_model")
class ExampleModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(localized = true, enterMessage = 'example.model.enter.message.name', enableClear = true)
    @ElementCollection
    @CollectionTable(name = 'example_localized_name',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'lang')
    @Column(name = 'name', columnDefinition = "TEXT")
    Map<String, String> name

    @Editable
    @Min(value = 2L, message = 'example.model.number.min.validation')
    @Max(value = 10L, message = 'example.model.number.max.validation')
    @Column(name = 'number')
    Integer number

    @Editable(enterMessage = 'example.model.enter.message.field1', enableClear = true)
    @Pattern(regexp = /..+/, message = 'example.model.field1.pattern.validation')
    @Column(name = 'field1')
    String field1

    @Editable(enterMessage = 'example.model.enter.message.flag')
    @Column(name = 'flag')
    Boolean flag

    @Column(name = 'service_field')
    String field2

    @Column(name = 'service_flag')
    String serviceFlag
}
