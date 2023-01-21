package com.khabaznia.bots.core.flow.annotation

import com.khabaznia.bots.core.flow.enums.FieldType
import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Editable {

    boolean id() default false

    boolean enableClear() default false

    FieldType type() default FieldType.STRING

    String enterMessage() default ''

    String fieldButtonMessage() default ''

    /**
     * COLLECTION field params
     */
    boolean canCreateEntity() default true

    String mappedBy() default ''
}
