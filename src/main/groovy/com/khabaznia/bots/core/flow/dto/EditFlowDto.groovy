package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
@ToString
abstract class EditFlowDto {

    Class entityClass
    String entityFactory

    String enterText
    Map<String, String> enterTextBinding = [:]

    String successPath
    Map<String, String> redirectParams = [:]

    String backPath
}
