package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'confirmationFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
@ToString
class ConfirmationFlowDto {

    String menuText
    Map<String, String> menuTextBinding = [:]

    String acceptPath
    String declinePath
    String backPath

    String acceptPathMessage
    String declinePathMessage
    String backPathMessage

    Map<String, String> redirectParams = [:]
}
