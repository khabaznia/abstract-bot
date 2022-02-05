package com.khabaznia.bot.core.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Works only with {@link com.khabaznia.bot.core.annotation.BotRequest}.
 * Used to create aliases for controller mapping with localized values.
 * Uses {@link com.khabaznia.bot.core.annotation.BotRequest#path} to find localized values in messages_*.properties
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface Localized {
}