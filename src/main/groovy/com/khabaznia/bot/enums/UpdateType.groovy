package com.khabaznia.bot.enums

import static com.khabaznia.bot.controller.Constants.COMMON.*

enum UpdateType {

    MESSAGE(ANY_STRING),
    IMAGE(IMAGE_CONTROLLER),
    VIDEO(VIDEO_CONTROLLER),
    AUDIO(AUDIO_CONTROLLER),
    NONE(DEFAULT)

    String defaultController

    UpdateType(String defaultController) {
        this.defaultController = defaultController
    }

}
