package com.khabaznia.bot.exception

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.UndeclaredThrowableException

class ExceptionUtil {

    static String getMessageFromUndeclaredThrowableException(UndeclaredThrowableException e) {
        def message = e.getMessage() ?: "Failed to execute some method. Check cause in logs."
        def throwable = e.getUndeclaredThrowable()
        if (throwable instanceof InvocationTargetException) {
            def targetException = throwable.getTargetException()
            if (targetException instanceof UndeclaredThrowableException) {
                message = targetException?.undeclaredThrowable?.getMessage()
            }
        } else {
            message = throwable.getMessage() ?: message
        }
        message
    }
}
