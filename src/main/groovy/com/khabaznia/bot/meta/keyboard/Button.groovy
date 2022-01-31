package com.khabaznia.bot.meta.keyboard

import com.khabaznia.bot.enums.ButtonType
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
abstract class Button {

    String id
    ButtonType type
    String key
    Map<String, String> binding
    String emoji

    Button() {
        binding = [:]
        id = UUID.randomUUID().toString()
        type = ButtonType.SIMPLE
    }
}
