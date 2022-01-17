package com.khabaznia.bot.core.annotation

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ActionType

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Action {

    ActionType actionType() default ActionType.TYPING
}