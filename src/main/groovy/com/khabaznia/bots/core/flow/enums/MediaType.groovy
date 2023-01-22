package com.khabaznia.bots.core.flow.enums

enum MediaType {

    IMAGE('text.edit.flow.validation.media.is.not.image', 'sendPhoto'),
    DOCUMENT('text.edit.flow.validation.media.is.not.document', 'sendDocument'),
    AUDIO('text.edit.flow.validation.media.is.not.audio', 'sendAudio'),
    ANIMATION('text.edit.flow.validation.media.is.not.animation', 'sendAnimation'),
    VIDEO('text.edit.flow.validation.media.is.not.video', 'sendVideo')

    String validationMessage
    String correspondingBeanName

    MediaType(String validationMessage, String correspondingBeanName) {
        this.validationMessage = validationMessage
        this.correspondingBeanName = correspondingBeanName
    }
}
