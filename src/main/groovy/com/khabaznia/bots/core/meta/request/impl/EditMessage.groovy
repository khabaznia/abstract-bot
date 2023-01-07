package com.khabaznia.bots.core.meta.request.impl

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.meta.response.impl.MessageResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'editMessage')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class EditMessage extends AbstractKeyboardMessage<MessageResponse> {

    String text
    String emoji
    Integer messageId
    Map<String, String> binding
    String label //label of message that should be in DB
    boolean disableWebPreview

    Set<MessageFeature> getFeatures() {
        super.features << MessageFeature.EDIT
    }

    EditMessage delete() {
        super.delete()
        this
    }
}
