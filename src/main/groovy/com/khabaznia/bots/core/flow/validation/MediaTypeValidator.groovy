package com.khabaznia.bots.core.flow.validation

import com.khabaznia.bots.core.flow.enums.MediaType
import org.hibernate.validator.internal.engine.ConstraintViolationImpl

import javax.validation.ConstraintViolationException

class MediaTypeValidator {

    static void validate(MediaType actualMediaType, MediaType expectedMediaType) {
        def errorMessage = null
        if (!actualMediaType)
            errorMessage = 'text.edit.flow.validation.is.not.media'
        if (actualMediaType != expectedMediaType)
            errorMessage = expectedMediaType.validationMessage
        if (errorMessage)
            throw new ConstraintViolationException(
                    [ConstraintViolationImpl.forBeanValidation(errorMessage, [:], [:], errorMessage,
                            null, null, null, null, null, null, null)
                    ].toSet()
            )
    }
}
