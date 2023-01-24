package com.khabaznia.bots.core.flow.strategy.impl.resolving

import com.khabaznia.bots.core.flow.enums.MediaType
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.meta.Emoji
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getEditableAnnotationForField
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getFieldClass

@Component('mediaFieldResolvingStrategy')
class MediaFieldResolvingStrategy extends FieldResolvingStrategy {

    Map<MediaType, String> mediaTypeEmoji =
            [(MediaType.IMAGE)    : Emoji.PHOTO,
             (MediaType.AUDIO)    : Emoji.AUDIO,
             (MediaType.VIDEO)    : Emoji.VIDEO,
             (MediaType.ANIMATION): Emoji.ANIMATION,
             (MediaType.DOCUMENT) : Emoji.DOCUMENT]

    Object getValue(Class entityClass, Long entityId, String fieldName) {
        def fieldClass = getFieldClass(entityClass, fieldName)
        fieldClass.valueOf(getPersistedValue(entityClass, entityId, fieldName))
    }

    @Override
    String getStringView(Object entity, String fieldName) {
        def mediaType = getEditableAnnotationForField(entity.class, fieldName).mediaType()
        entity."$fieldName" ? mediaTypeEmoji.get(mediaType) : null
    }
}
