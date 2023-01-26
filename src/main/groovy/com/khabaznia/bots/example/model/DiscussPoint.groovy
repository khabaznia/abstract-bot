package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern

import static com.khabaznia.bots.core.flow.enums.FieldType.LOCALIZED
import static com.khabaznia.bots.core.flow.enums.FieldType.NUMBER

@ToString(excludes = ['meeting'])
@Editable(entityFactory = 'discussPointFactory')
@Entity(name = "discuss_point")
class DiscussPoint {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(id = true,
            fieldButtonMessage = 'discuss.point.title.name',
            enterMessage = 'discuss.point.title.enter.message')
    @Pattern(regexp = /^(.+) (.+)+$/, message = 'discuss.point.title.validation.title.is.too.small')
    @Column
    String title

    @Editable(enableClear = true, type = LOCALIZED,
            fieldButtonMessage = 'discuss.point.description.name',
            enterMessage = 'discuss.point.description.enter.message')
    @ElementCollection
    @CollectionTable(name = 'discuss_point_localized_description',
            joinColumns = [@JoinColumn(name = 'id', referencedColumnName = 'id')])
    @MapKeyColumn(name = 'lang')
    @Column(name = 'description', columnDefinition = "TEXT")
    Map<String, String> description = [:]

    @Editable(type = NUMBER, enableClear = true,
            fieldButtonMessage = 'discuss.point.priority.name',
            enterMessage = 'discuss.point.priority.enter.message')
    @Min(value = 0L, message = 'discuss.point.priority.validation.should.not.be.negative')
    @Column(name = 'priority')
    Integer priority

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'meeting')
    Meeting meeting

    @Column
    String userCode
}
