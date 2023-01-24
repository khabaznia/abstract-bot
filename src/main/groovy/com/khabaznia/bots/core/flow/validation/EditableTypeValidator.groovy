package com.khabaznia.bots.core.flow.validation

import com.khabaznia.bots.core.exception.BotException
import com.khabaznia.bots.core.flow.annotation.Editable
import com.khabaznia.bots.core.flow.enums.FieldType
import groovy.util.logging.Slf4j
import org.reflections.Reflections

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getEntityIdField
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getSelectableFieldEntityClass

@Slf4j
class EditableTypeValidator {

    static void validateEditableTypes() {
        def reflections = new Reflections('com.khabaznia.bots')
        def annotatedFields = reflections
                .getTypesAnnotatedWith(Editable.class)*.declaredFields.
                flatten().findAll { it.getAnnotation(Editable.class) != null }
        reflections.getTypesAnnotatedWith(Editable.class).findAll().each { validateType(it as Class) }
        annotatedFields.each { validateField(it) }
    }

    private static void validateType(Class editableClass, boolean isSelectable = false) {
        log.info 'Validating of class {}- {}', isSelectable ? 'from selectable field ' : '', editableClass.simpleName
        def classSimpleName = editableClass.simpleName
        if (!getEntityIdField(editableClass))
            throw new BotException("Class $classSimpleName should have Editable field marked as id")
        if (!isOneIdField(editableClass))
            throw new BotException("Class $classSimpleName should have only one id field")
        if (isActualCollectionType(getEntityIdField(editableClass)))
            throw new BotException("Id field in class $classSimpleName should not be collection.")
    }

    private static void validateField(Field field) {
        log.info 'Validating {}', fieldLog(field)
        def fieldAnnotation = field.getAnnotation(Editable.class)
        if (Number.isAssignableFrom(field.type) && fieldAnnotation.type() != FieldType.NUMBER)
            throw new BotException("Number ${fieldLog(field)} should be marked as FieldType.NUMBER")
        if (Boolean.isAssignableFrom(field.type) && fieldAnnotation.type() != FieldType.BOOLEAN)
            throw new BotException("Boolean ${fieldLog(field)} should be marked as FieldType.BOOLEAN")
        if (isActualCollectionType(field) && fieldAnnotation.type() != FieldType.COLLECTION)
            throw new BotException("Collection ${fieldLog(field)} should be marked as FieldType.COLLECTION")
        if (fieldAnnotation.type() == FieldType.COLLECTION) validateSelectableField(field)
    }

    private static void validateSelectableField(Field field) {
        def fieldAnnotation = field.getAnnotation(Editable.class)
        def selectableEntityClass = getSelectableFieldEntityClass(field.declaringClass as Class, field.name)
        if (fieldAnnotation.mappedBy().isEmpty())
            throw new BotException("Collection ${fieldLog(field)} should have mappedBy parameter in Editable annotation")
        if (!isFieldExistsInField(selectableEntityClass, fieldAnnotation.mappedBy()))
            throw new BotException("Collection ${fieldLog(field)} should be mapped to existing field. Field with name ${fieldAnnotation.mappedBy()} doesn't exists")
        validateType(selectableEntityClass, true)
    }

    private static boolean isOneIdField(Class editableClass) {
        editableClass.getDeclaredFields()
                .count { it.getAnnotation(Editable.class)?.id() } == 1
    }

    private static boolean isFieldExistsInField(Class clazz, String fieldName) {
        clazz.getDeclaredFields()*.name.any { it == fieldName }
    }

    private static boolean isActualCollectionType(Field field) {
        def parameterizedType = field.genericType
        parameterizedType instanceof ParameterizedType
                && Collection.class.isAssignableFrom(parameterizedType.rawType)
    }

    private static String fieldLog(Field field) {
        def classSimpleName = ((Class) field.declaringClass).simpleName
        "field {$field.name} in class {$classSimpleName}"
    }
}
