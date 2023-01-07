package com.khabaznia.bots.core.enums

enum ButtonType {

    SIMPLE(''), ONE_TIME('otb'), SWITCH('swb')

    String paramKey

    ButtonType(String paramKey) {
        this.paramKey = paramKey
    }
}