package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'editFieldFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
@ToString(includeSuper = true)
class EditFieldFlowDto extends EditFlowDto{

    Object entityToEdit
    String fieldName
    String successText
}
