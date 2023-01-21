package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.model.EditFlow

import javax.persistence.Entity
import javax.persistence.ManyToMany
import java.lang.reflect.Field

import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat

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

    static Editable getCurrentFieldAnnotation() {
        editFlowField.getAnnotation(Editable.class)
    }

    static Class getSelectableFieldEntityClass() {
        editFlowField.getGenericType().actualTypeArguments[0] as Class<?>
    }

    static String getSelectableFieldHTableName() {
        selectableFieldEntityClass.getAnnotation(Entity).name()
    }

    static Boolean getSelectableFieldHIsManyToManyRelation() {
        editFlowField.getAnnotation(ManyToMany.class) != null
    }

    static Class getFieldClass(Class entityClass, String fieldName) {
        entityClass.getDeclaredField(fieldName).type
    }

    static Map<String, String> getEditableFields(Class entityClass) {
        entityClass.getDeclaredFields()
                .findAll { it.getAnnotation(Editable.class) != null }
                .collectEntries { [(it.name): (it.getAnnotation(Editable.class).fieldButtonMessage() ?: it.name)] }
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

    static String getEntityFactory(Class entityClass) {
        ((Editable) entityClass.getAnnotation(Editable.class))?.entityFactory() ?:
                Editable.class.getDeclaredMethod('entityFactory').getDefaultValue() as String
    }

    private static Field getEntityIdField(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
    }

    private static Field getEditFlowField() {
        getClass(currentEditFlow).getDeclaredField(currentEditFlow.fieldName)
    }
}
