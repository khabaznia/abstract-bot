package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Dto intended to collect data for generation edit entity flow
 */
@Component(value = 'editEntityFlowDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
@ToString(includeSuper = true)
class EditEntityFlowDto extends EditFlowDto {

    /**
     * Either {@link EditEntityFlowDto#entityToEdit}
     * nor {@link EditEntityFlowDto#entityId} and {@link EditEntityFlowDto#entityClass} be specified
     */
    Object entityToEdit
    // OR set class and id
    Long entityId

    Integer fieldsInRow
}
