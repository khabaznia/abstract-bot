package com.khabaznia.bot.core.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import static com.khabaznia.bot.controller.Constants.COMMON.EMPTY_PATH

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface BotRequest {

    String path() default EMPTY_PATH

    String after() default ''
}