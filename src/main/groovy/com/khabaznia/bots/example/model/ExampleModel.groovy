package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern

import static com.khabaznia.bots.core.flow.enums.FieldType.*

@ToString
@Entity(name = "example_model")
class ExampleModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(enableClear = true, type = LOCALIZED,
            fieldButtonMessage = 'example.model.localized.name.button.name',
            enterMessage = 'example.model.enter.message.name')
    @ElementCollection
    @CollectionTable(name = 'example_localized_name',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'lang')
    @Column(name = 'name', columnDefinition = "TEXT")
    Map<String, String> name = [:]

    @Editable(fieldButtonMessage = 'example.model.number.button.name', type = NUMBER)
    @Min(value = 2L, message = 'example.model.number.min.validation')
    @Max(value = 10L, message = 'example.model.number.max.validation')
    @Column(name = 'number')
    Integer number

    @Editable(enableClear = true, id = true,
            fieldButtonMessage = 'example.model.field1.button.name',
            enterMessage = 'example.model.enter.message.field1')
    @Pattern(regexp = /..+/, message = 'example.model.field1.pattern.validation')
    @Column(name = 'field1')
    String field1

    @Editable(enterMessage = 'example.model.enter.message.flag', type = BOOLEAN)
    @Column(name = 'flag')
    Boolean flag

    @Editable(type = MEDIA, enableClear = true)
    @Column
    String someMedia

    @Column(name = 'service_field')
    String field2

    @Column(name = 'service_flag')
    String serviceFlag

    @Column
    String userCode

    @Editable(type = COLLECTION, mappedBy = 'parent', selectionStrategy = 'exampleModelEntryUserSelectionStrategy',
            enterMessage = '')
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Column(name = "entries")
    Set<ExampleModelEntry> entries

    @Editable(type = COLLECTION, mappedBy = 'manyExamples',
            selectionStrategy = 'exampleModelEntryAbbreviationSelectionStrategy',
            enterMessage = 'Here is only entries with abreviation. Select some. \nWhen unselect, abbreviation is deleted',
            fieldButtonMessage = 'OTHER_ENTRIES')
    @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH], fetch = FetchType.LAZY)
    @JoinTable(
            name = 'many_entries',
            joinColumns = @JoinColumn(name = 'example_id', referencedColumnName = 'id'),
            inverseJoinColumns = @JoinColumn(name = "example_entry_id", referencedColumnName = 'id'))
    List<ExampleModelEntry> manyEntries
}
