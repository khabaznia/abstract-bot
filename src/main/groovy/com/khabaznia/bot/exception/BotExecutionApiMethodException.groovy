package com.khabaznia.bot.exception

class BotExecutionApiMethodException extends BotException {

    BotExecutionApiMethodException(String message) {
        super(message)
    }

    BotExecutionApiMethodException(String message, Throwable cause) {
        super(message, cause)
    }
}
