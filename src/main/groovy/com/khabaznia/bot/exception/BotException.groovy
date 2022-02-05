package com.khabaznia.bot.exception

class BotException extends Exception {

    BotException(String message) {
        super(message)
    }

    BotException(String message, Throwable cause) {
        super(message, cause)
    }
}
