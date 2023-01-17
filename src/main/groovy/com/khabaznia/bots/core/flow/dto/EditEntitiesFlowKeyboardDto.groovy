package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.function.Function

@Component(value = 'editEntitiesFlowKeyboardDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor()
@ToString()
class EditEntitiesFlowKeyboardDto<T> {

    /**
     * Mandatory fields
     */
    Class<T> entityClass
    List<T> entities
    Function<T, String> buttonNameRetrieverFunction
    String thisStepPath

    /**
     * Optional fields
     */
    String backPath
    String createNewEntitySuccessMessage
    String deleteEntitySuccessMessage
    Map<String, String> redirectParams = [:]
    boolean canDeleteEntities = true
    boolean canCreateNewEntity = true
}
