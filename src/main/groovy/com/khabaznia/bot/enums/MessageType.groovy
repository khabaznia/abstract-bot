package com.khabaznia.bot.enums

enum MessageType {

    SKIP, //Do not save in DB
    PERSIST, // Just save to DB
    DELETE, // Delete with next message
    REPLY_KEYBOARD, // Message with reply keyboard
    FORCE_DELETE_INLINE_KEYBOARD, // Message with inline keyboard that should be deleted with next message
    ONE_TIME_INLINE_KEYBOARD, // Message with inline keyboard that should be deleted after any button press
    PINNED, // Pinned message
}
