package com.khabaznia.bots.core.routing.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import static com.khabaznia.bots.common.Constants.COMMON.ANY_STRING

/**
 * Marks method in {@link com.khabaznia.bots.core.routing.annotation.BotController } as mapping for some command.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface BotRequest {

    /**
     * Command mapping. Should be unique
     */
    String path() default ANY_STRING

    /**
     * Specifies previous path. The method will be invoked anly if previous request of this user was command that specified in this field
     */
    String after() default ''

    /**
     * Enables repeating of the request for same user. Works only if feature enabled in 'block.duplicate.requests' configuration
     */
    boolean enableDuplicateRequests() default false

    boolean rawParams() default false
}