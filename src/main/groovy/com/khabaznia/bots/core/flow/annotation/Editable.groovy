package com.khabaznia.bots.core.flow.annotation

import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.enums.MediaType

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.FIELD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface Editable {

    boolean id() default false

    boolean enableClear() default false

    FieldType type() default FieldType.STRING

    String enterMessage() default ''

    String fieldButtonMessage() default ''

    String entityFactory() default 'defaultEntityFactory'

    String entityViewHeader() default 'text.entity.view.header'

    /**
     * COLLECTION field params
     */
    boolean canCreateEntity() default true

    String mappedBy() default ''

    String selectionStrategy() default 'defaultFieldSelectionStrategy'

    /**
     * MEDIA field params
     */

    MediaType mediaType() default MediaType.IMAGE

    boolean viewOnly() default false
}
