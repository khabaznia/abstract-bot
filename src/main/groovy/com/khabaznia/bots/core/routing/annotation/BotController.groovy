package com.khabaznia.bots.core.routing.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Intended to mark class as controller that aggregates methods with {@link com.khabaznia.bots.core.routing.annotation.BotRequest} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface BotController {

    /**
     * Specify path for controller, that will be prefix for all mappings in controller except marked with {@link com.khabaznia.bots.core.routing.annotation.Localized}
     */
    @Deprecated
    String path() default ''
}