package com.khabaznia.bots.core.meta.request.impl

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@ToString(includeSuper = true, includeNames = true)
abstract class AbstractMediaRequest<T extends MessageResponse> extends AbstractKeyboardMessage {

    //     for resource strategy file should be without prefix: e.g. file - help.gif, fileIdentifier - media_help.gif
    //     for api strategy should be used as it is returned from dto
    //     !!! don't use media id's longer that 60 symbols
    String fileIdentifier
    String label
    String text
    String emoji
    Map<String, String> binding = [:]
    String messageLabel
    boolean isInternal = false

    AbstractMediaRequest internal() {
        isInternal = true
        this
    }

    Set<MessageFeature> getFeatures() {
        super.features << MessageFeature.MEDIA
    }

    AbstractMediaRequest delete() {
        super.delete()
        this
    }
}
