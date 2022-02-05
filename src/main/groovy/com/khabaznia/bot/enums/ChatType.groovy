package com.khabaznia.bot.enums

enum ChatType {

    PRIVATE('private'), GROUP('group')

    String value

    ChatType(String value) {
        this.value = value
    }
}