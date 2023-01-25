package com.khabaznia.bots.core.flow.annotation

import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.enums.MediaType

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation is intended to mark types and fields that can be used for CRUD operations via bot edit flow feature.
 */
@Target([ElementType.FIELD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface Editable {

    boolean id() default false
    /**
     * In 'edit field menu' adds button to clear current value
     */
    boolean enableClear() default false
    /**
     * Specifies type of field
     */
    FieldType type() default FieldType.STRING
    /**
     * Specifies message property or text that will be on enter step, instead of default value.
     */
    String enterMessage() default ''
    /**
     * Specifies message property or text that will be dislplayed as field/button name in 'edit entity menu'
     */
    String fieldButtonMessage() default ''
    /**
     * Bean name of specific entity factory. Enables to override entity view and
     * default creation of instances of this type. Should extend {@link com.khabaznia.bots.core.flow.factory.EntityFactory}
     *
     * Should be defined on Type.
     */
    String entityFactory() default 'defaultEntityFactory'
    /**
     * Specifies message property or text that override first line in 'entity view'.
     * Just specify what is the instance. e.g: 'Song: ', 'Location: '
     */
    String entityViewHeader() default 'text.entity.view.header'
    /**
     * Marks field to use it only in 'entity view'
     */
    boolean viewOnly() default false

    // COLLECTION field params
    /**
     * Enables create entities of field collection type. Adds 'Create new' button.
     */
    boolean canCreateEntity() default true
    /**
     * (Mandatory) for collection field
     * Specifies reference in related collection type.
     */
    String mappedBy() default ''
    /**
     * Bean name of selection strategy.
     * Enables:
     *  - specifying entries that should be shown in selection. (For example sorting by user, e.g)
     *  - specifying logic on adding/removing entity from collection.
     *
     *  Should extend {@link com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy}
     */
    String selectionStrategy() default 'defaultFieldSelectionStrategy'
    /**
     * Specifies class of entity, if collection of id's saved instead of relation
     */
    Class selectableEntityClass() default Object.class

    // MEDIA field params
    /**
     * (Mandatory) for media field
     * Specifies type of Media. Needed for validation and correct displaying in 'entity view'
     */
    MediaType mediaType() default MediaType.IMAGE
}
