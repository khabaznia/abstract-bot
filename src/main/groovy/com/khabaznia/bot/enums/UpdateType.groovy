package com.khabaznia.bot.enums

import static com.khabaznia.bot.controller.Constants.COMMON.AUDIO_CONTROLLER
import static com.khabaznia.bot.controller.Constants.COMMON.ANY_STRING
import static com.khabaznia.bot.controller.Constants.COMMON.IMAGE_CONTROLLER
import static com.khabaznia.bot.controller.Constants.COMMON.VIDEO_CONTROLLER
import static com.khabaznia.bot.controller.Constants.COMMON.DEFAULT

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
