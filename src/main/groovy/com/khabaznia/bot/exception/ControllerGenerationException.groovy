package com.khabaznia.bot.exception


class ControllerGenerationException extends BotException {

    ControllerGenerationException(String message) {
        super(message)
    }

    ControllerGenerationException(String message, Throwable cause) {
        super(message, cause)
    }
}
