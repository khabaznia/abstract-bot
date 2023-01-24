package com.khabaznia.bots.core.flow.dto

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import java.util.function.Function

/**
 * Dto intended to collect all data for generating edit flow keyboard buttons for entities of specific class
 * @param <T>
 */
@Component(value = 'editEntitiesFlowKeyboardDto')
@Scope("prototype")
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor()
@ToString()
class EditEntitiesFlowKeyboardDto<T> {

    @NotNull
    Class<T> entityClass
    @NotNull
    List<T> entities
    @NotNull
    String thisStepPath

    /**
     * (Optional) Specifies function to retrieve name of button for each entity
     */
    Function<T, String> entityNameRetriever
    /**
     * (Optional) Will show back path button in bottom row. Default - no button
     */
    String backPath
    /**
     * (Optional)
     */
    String createNewEntitySuccessMessage
    /**
     * (Optional)
     */
    String deleteEntitySuccessMessage
    /**
     * (Optional) Adds button to same row for each entity to enable delete selected instance
     */
    boolean canDeleteEntities = true
    /**
     * (Optional) Adds button on the top of menu that enables to create new instance of same class
     */
    boolean canCreateNewEntity = true
    /**
     * (Optional)
     */
    Map<String, String> redirectParams = [:]
    /**
     * (Optional) Bean name of entity factory. (Custom construction and view can be specified)
     */
    String entityFactory
    /**
     * (Optional) Specifies columns number in editEntity menu (editing fields of concrete entity).  Default - 3
     */
    Integer fieldsInRow
    /**
     * (Optional) Specified columns number in NOTE: value is ignored when entity can be deleted. Default - 1 row for each entity
     */
    Integer entitiesInRow
}
