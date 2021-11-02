package com.khabaznia.bot.model

enum ChatType {

    PRIVATE('private'),
    GROUP('group')

    String name

    ChatType(final String name) {
        this.name = name
    }
}