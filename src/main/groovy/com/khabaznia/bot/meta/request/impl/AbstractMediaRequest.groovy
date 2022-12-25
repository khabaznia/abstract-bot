package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.enums.MessageFeature
import com.khabaznia.bot.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@ToString(includeSuper = true, includeNames = true)
abstract class AbstractMediaRequest<T extends MessageResponse> extends AbstractKeyboardMessage {

    // to use this - media should be in format "media_".
    //     for resource strategy file should be without prefix: e.g. file - help.gif, fileIdentifier - media_help.gif
    //     for api strategy should be used as it is returned from dto
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

    Set<MessageFeature> getFeatures(){
        super.features << MessageFeature.MEDIA
    }

    AbstractMediaRequest delete() {
        super.delete()
        this
    }
}
