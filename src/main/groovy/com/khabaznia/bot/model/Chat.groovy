package com.khabaznia.bot.model

class Chat {

    Long code
    String lang
    String lastAction
    ChatType type
    ChatRole role

    List<Message> messages
    List<User> users
}
