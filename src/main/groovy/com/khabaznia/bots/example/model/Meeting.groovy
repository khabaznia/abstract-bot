package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import groovy.transform.ToString

import javax.persistence.*

import static com.khabaznia.bots.core.flow.enums.FieldType.*
import static com.khabaznia.bots.core.flow.enums.MediaType.DOCUMENT

@ToString(excludes = ['pointsToDiscuss'])
@Editable
@Entity(name = "meeting")
class Meeting {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(enableClear = false, id = true)
    @Column(name = 'meeting_title')
    String title

    @Editable(type = MEDIA, mediaType = DOCUMENT, enableClear = true)
    @Column
    String attachment

    @Editable(viewOnly = true, type = BOOLEAN)
    @Column(name = 'upcoming')
    Boolean upcoming

    @Editable(type = SELECTIVE, mappedBy = 'meeting', selectionStrategy = 'pointsSelectionStrategy')
    @OneToMany(mappedBy = "meeting", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Column(name = "points_to_discuss")
    Set<DiscussPoint> pointsToDiscuss

    @Column
    String userCode
}
