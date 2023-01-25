package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.meta.response.dto.User
import groovy.transform.ToString

import javax.persistence.*

import static com.khabaznia.bots.core.flow.enums.FieldType.*
import static com.khabaznia.bots.core.flow.enums.MediaType.DOCUMENT
import static com.khabaznia.bots.core.flow.enums.MediaType.IMAGE

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

    @Editable(type = SELECTIVE, mappedBy = 'meeting', selectionStrategy = 'meetingSelectionStrategy')
    @OneToMany(mappedBy = "meeting", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Column(name = "points_to_discuss")
    Set<DiscussPoint> pointsToDiscuss

    @Column
    String userCode
}
