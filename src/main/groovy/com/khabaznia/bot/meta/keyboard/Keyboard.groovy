package com.khabaznia.bot.meta.keyboard

import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@ToString
abstract class Keyboard<T extends Button> {

    @Autowired
    ApplicationContext context

    List<List<T>> rows
    List<T> currentRow

    abstract Keyboard row()

    abstract List<List<T>> get()

    abstract protected T getButton()
}
