package com.khabaznia.bot.exception

class BotServiceException extends BotException {

    Map<String, String> binding
    boolean logConsoleOnly

    BotServiceException(String message, Map<String, String> binding = [:], boolean logConsoleOnly = false) {
        super(message)
        this.binding = binding
        this.logConsoleOnly = logConsoleOnly
    }
}
