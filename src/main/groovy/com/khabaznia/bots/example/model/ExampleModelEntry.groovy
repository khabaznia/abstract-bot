package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.enums.FieldType
import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.Pattern

import static com.khabaznia.bots.core.flow.enums.FieldType.MEDIA
import static com.khabaznia.bots.core.flow.enums.MediaType.AUDIO

@ToString
@Editable(entityFactory = 'exampleModelEntryFactory')
@Entity(name = "example_model_entry")
class ExampleModelEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(enableClear = true, type = FieldType.LOCALIZED,
            fieldButtonMessage = 'example.model.localized.name.button.name',
            enterMessage = 'example.model.enter.message.name')
    @ElementCollection
    @CollectionTable(name = 'example_model_entry_localized_name',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'lang')
    @Column(name = 'name', columnDefinition = "TEXT")
    Map<String, String> name = [:]

    @Editable(enableClear = true, id = true,
            fieldButtonMessage = 'example.model.model.entry.button.name')
    @Pattern(regexp = /^[A-Z]+$/, message = 'example.model.model.entry.pattern.validation')
    @Column(name = 'field1')
    String abbreviation

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'parent')
    ExampleModel parent

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = 'manyEntries')
    List<ExampleModel> manyExamples

    @Column
    String userCode

    @Editable(type = MEDIA, enableClear = true, mediaType = AUDIO)
    @Column
    String someMedia
}
