package com.khabaznia.bots.core.dto

import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'fieldEditFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class FieldEditFlowDto {

    Long entityId
    String fieldName
    Boolean localized
    String lang
    String successPath

    String repoBeanId
    String validationMethod
    String enterMessage
    String successMessage
}
