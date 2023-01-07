package com.khabaznia.bots.core.routing.annotation

import com.khabaznia.bots.core.enums.Role
import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Specifies roles that can access to the method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Secured {

    Role[] roles() default [Role.ALL]
}