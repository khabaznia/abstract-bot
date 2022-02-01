package com.khabaznia.bot.core.annotation

import com.khabaznia.bot.enums.Role
import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Secured {

    Role[] roles() default [Role.ALL]
}