package com.khabaznia.bot.dto

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'confirmationFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class ConfirmationFlowDto {

    String menuText
    Map<String, String> menuTextBinding = [:]

    String acceptPath
    String declinePath
    String backPath

    String acceptPathMessage
    String declinePathMessage
    String backPathMessage

    Map<String, String> params = [:]
}
