package com.khabaznia.bots.core.meta.keyboard.impl

import com.khabaznia.bots.core.meta.keyboard.Button
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.BUTTON_PARAMETERS.BUTTON_ID

@Component(value = 'inlineButton')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor
class InlineButton extends Button {

    Map<String, String> params
    String callbackData
    String url

    InlineButton() {
        super()
        params = [:]
        params.put(BUTTON_ID, this.id)
    }

    InlineButton params(Map<String, String> params) {
        this.params.putAll(params)
        return this
    }

    InlineButton text(String text) {
        super.text = text
        return this
    }

    @Override
    String toString() {
        return "$id $type || $text $emoji $binding || $url $callbackData $params"
    }
}
