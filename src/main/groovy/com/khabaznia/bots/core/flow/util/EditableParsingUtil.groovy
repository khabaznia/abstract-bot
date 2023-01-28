package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.model.EditFlow

import javax.persistence.Entity
import javax.persistence.ManyToMany
import java.lang.reflect.Field

import static com.khabaznia.bots.core.util.BotSession.getCurrentChat

class EditableParsingUtil {

    static getCurrentEditFlow() {
        def flow = currentChat.editFlow
        flow.childFlow ?: flow
    }

    static Class<?> getClass(EditFlow editFlow) {
        Class.forName(editFlow.entityClassName)
    }

    static FieldType getFieldType(Class entityClass, String fieldName) {
        entityClass.getDeclaredFields()
                .find { it.name == fieldName }
                .getAnnotation(Editable.class)?.type()
    }

    static String getEnterMessage() {
        getCurrentFieldAnnotation().enterMessage()
    }

    static boolean isValueClearingEnabled() {
        getCurrentFieldAnnotation().enableClear()
    }

    static Editable getCurrentFieldAnnotation(EditFlow editFlow = currentEditFlow) {
        getEditFlowField(editFlow).getAnnotation(Editable.class)
    }

    static Class getSelectableFieldEntityClass(EditFlow editFlow = currentEditFlow) {
        getEditFlowField(editFlow).getGenericType().actualTypeArguments[0] as Class<?>
    }

    static Class getSelectableFieldEntityClass(Class entityClass, String fieldName) {
        def field = entityClass.getDeclaredField(fieldName)
        def fieldClass = field.getAnnotation(Editable.class).selectableEntityClass()
        fieldClass != Object.class ? fieldClass :
                field.getGenericType().actualTypeArguments[0] as Class<?>
    }

    static String getSelectableFieldHTableName() {
        getSelectableFieldEntityClass().getAnnotation(Entity).name()
    }

    static Boolean getSelectableFieldHIsManyToManyRelation() {
        getEditFlowField().getAnnotation(ManyToMany.class) != null
    }

    static Class getFieldClass(Class entityClass, String fieldName) {
        entityClass.getDeclaredField(fieldName).type
    }

    static Map<String, String> getEditableFields(Class entityClass) {
        entityClass.getDeclaredFields()
                .findAll { it.getAnnotation(Editable.class) != null }
                .findAll { !it.getAnnotation(Editable.class).viewOnly() }
                .collectEntries { [(it.name): (it.getAnnotation(Editable.class).fieldButtonMessage() ?: it.name)] }
    }

    static List<String> getViewFields(Class entityClass) {
        entityClass.getDeclaredFields()
                .findAll { it.getAnnotation(Editable.class) != null }
                .collect { it.name }
    }

    static String getEntityEditableIdFieldName(Class entityClass) {
        getEntityIdField(entityClass).name
    }

    static String getDefaultMessageOfIdField(Class entityClass) {
        getEntityIdField(entityClass).getAnnotation(Editable.class).fieldButtonMessage()
    }

    static Editable getEntityIdFieldAnnotation(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
                .getAnnotation(Editable.class)
    }

    static String getEntityFactoryName(Class entityClass) {
        getEditableAnnotationForClass(entityClass)?.entityFactory() ?:
                Editable.class.getDeclaredMethod('entityFactory').getDefaultValue() as String
    }

    static String getEntityViewHeader(Class entityClass) {
        getEditableAnnotationForClass(entityClass)?.entityViewHeader() ?:
                Editable.class.getDeclaredMethod('entityViewHeader').getDefaultValue() as String
    }

    static Editable getEditableAnnotationForField(Class entityClass, String fieldName) {
        entityClass.getDeclaredField(fieldName).getAnnotation(Editable.class)
    }

    static String getFieldSelectionStrategyName(EditFlow editFlow = currentEditFlow) {
        editFlow.fieldSelectionStrategy ?: getCurrentFieldAnnotation(editFlow).selectionStrategy()
    }

    static Field getEntityIdField(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
    }

    private static Editable getEditableAnnotationForClass(Class entityClass) {
        (Editable) entityClass.getAnnotation(Editable.class)
    }

    private static Field getEditFlowField(EditFlow editFlow = currentEditFlow) {
        getClass(editFlow).getDeclaredField(editFlow.fieldName)
    }
}
