package com.khabaznia.bots.example.model

import com.khabaznia.bots.core.flow.annotation.Editable
import groovy.transform.ToString

import javax.persistence.*

import static com.khabaznia.bots.core.flow.enums.FieldType.*
import static com.khabaznia.bots.core.flow.enums.MediaType.DOCUMENT

@ToString(excludes = ['pointsToDiscuss'])
@Editable(entityViewHeader = 'meeting.header')
@Entity(name = "meeting")
class Meeting {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Editable(enableClear = false, id = true, fieldButtonMessage = 'meeting.title.name')
    @Column(name = 'meeting_title')
    String title

    @Editable(type = MEDIA, mediaType = DOCUMENT,
            enableClear = true,
            fieldButtonMessage = 'meeting.attachment.name')
    @Column
    String attachment

    @Editable(viewOnly = true, type = BOOLEAN, fieldButtonMessage = 'meeting.upcoming.name')
    @Column(name = 'upcoming')
    Boolean upcoming

    @Editable(type = SELECTIVE, mappedBy = 'meeting',
            selectionStrategy = 'pointsSelectionStrategy',
            fieldButtonMessage = 'meeting.points.to.discuss.name',
            enterMessage = 'meeting.points.to.discuss.enter.message')
    @OneToMany(mappedBy = "meeting", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Column(name = "points_to_discuss")
    Set<DiscussPoint> pointsToDiscuss

    @Column
    String userCode
}
