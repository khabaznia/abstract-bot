package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Dto intended to collect data for generation edit field flow
 */
@Component(value = 'editFieldFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
@ToString(includeSuper = true)
class EditFieldFlowDto extends EditFlowDto {

    /**
     * Either {@link EditFieldFlowDto#entityToEdit}
     * nor {@link EditFieldFlowDto#entityId} and {@link EditFieldFlowDto#entityClass} be specified
     */
    Object entityToEdit
    // OR set class and id
    Long entityId

    String fieldName
    String successText
}
