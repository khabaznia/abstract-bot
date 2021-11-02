package com.khabaznia.bot.core

class ControllerGenerationException extends Exception {

    ControllerGenerationException(final String message) {
        super(message)
    }

    ControllerGenerationException(final String message, final Throwable cause) {
        super(message, cause)
    }
}
