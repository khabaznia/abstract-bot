package com.khabaznia.bots.core.flow.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Editable {

    boolean localized() default false

    boolean enableClear() default false

    boolean id() default false

    String enterMessage() default ''

    String fieldButtonMessage() default ''
}
