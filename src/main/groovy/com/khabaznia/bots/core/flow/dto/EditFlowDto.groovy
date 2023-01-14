package com.khabaznia.bots.core.flow.dto

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'editFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class EditFlowDto {

    // EXTENDED FLOW: add or edit one from existing id DB
    Class entityClass

    // SPECIFIC ENTITY
    Object entityToEdit
    String fieldName

    String enterText
    Map<String, String> enterTextBinding = [:]

    String successPath
    String successText

    Map<String, String> params = [:]
}
