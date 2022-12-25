package com.khabaznia.bot.exception

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.UndeclaredThrowableException

class ExceptionUtil {

    static String getMessageFromUndeclaredThrowableException(UndeclaredThrowableException ex) {
        def defaultMessage = ex.getMessage() ?: "Failed to execute some method. Check cause in logs."
        def targetException = getTargetException(ex)
        (targetException instanceof UndeclaredThrowableException)
                ? targetException?.undeclaredThrowable?.getMessage()
                : targetException.getMessage() ?: defaultMessage
    }

    static boolean isLogEventException(Throwable ex) {
        ex instanceof UndeclaredThrowableException &&
                ex?.undeclaredThrowable instanceof BotLogEventException
    }

    static boolean isBotServiceException(UndeclaredThrowableException ex) {
        getTargetException(ex) instanceof BotServiceException
    }

    static Throwable getTargetException(UndeclaredThrowableException ex) {
        def throwable = ex.getUndeclaredThrowable()
        throwable instanceof InvocationTargetException
                ? throwable.getTargetException()
                : throwable
    }
}
