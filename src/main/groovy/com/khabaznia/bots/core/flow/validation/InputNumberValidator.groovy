package com.khabaznia.bots.core.flow.validation

import org.hibernate.validator.internal.engine.ConstraintViolationImpl

import javax.validation.ConstraintViolationException

class InputNumberValidator {

    static void validate(String value, Class clazz) {
        try {
            clazz.valueOf(value)
        } catch (NumberFormatException ex) {
            throw new ConstraintViolationException(
                    [ConstraintViolationImpl.forBeanValidation('text.edit.flow.validation.input.is.not.number',
                            [:], [:], 'text.edit.flow.validation.input.is.not.number',
                            null, null, null, null, null, null, null)
                    ].toSet()
            )
        }
    }
}
