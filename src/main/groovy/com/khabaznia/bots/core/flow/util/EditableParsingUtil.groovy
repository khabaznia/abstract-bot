package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.model.EditFlow

import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat

class EditableParsingUtil {

    static getCurrentEditFlow() {
        def flow = currentChat.editFlow
        flow.type != FieldType.COLLECTION ? flow : flow.childFlow
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
        getClass(currentEditFlow).getDeclaredField(currentEditFlow.fieldName)
                .getAnnotation(Editable.class)
    }

    static Map<String, String> getEditableFields(Class entityClass) {
        entityClass.getDeclaredFields()
                .findAll { it.getAnnotation(Editable.class) != null }
                .collectEntries { [(it.name): (it.getAnnotation(Editable.class).fieldButtonMessage() ?: it.name)] }
    }

    static String getEntityEditableIdFieldName(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
                .name
    }

    static String getDefaultMessageOfIdField(Class entityClass) {
        entityClass.getDeclaredFields()
                .find { it.getAnnotation(Editable.class)?.id() }
                .getAnnotation(Editable.class).fieldButtonMessage()
    }
}
