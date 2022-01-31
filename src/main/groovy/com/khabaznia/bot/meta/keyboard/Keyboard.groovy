package com.khabaznia.bot.meta.keyboard

import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@ToString
abstract class Keyboard<T extends Button> {

    @Autowired
    protected ApplicationContext context

    protected List<List<T>> rows
    protected List<T> currentRow

    abstract Keyboard row()

    abstract List<List<T>> get()

    Keyboard setRows(List<List<T>> rows) {
        this.rows = rows
        this
    }

    abstract protected T getButton()
}
