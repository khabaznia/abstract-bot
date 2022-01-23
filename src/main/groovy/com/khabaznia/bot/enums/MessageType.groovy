package com.khabaznia.bot.enums

enum MessageType {

    SKIP, //Do not save in DB
    PERSIST, // Just save to DB
    DELETE, // Delete with next message
    INLINE_KEYBOARD, // Regular inline keyboard, that can be updated
    ONE_TIME_INLINE_KEYBOARD, // Message with inline keyboard that should be deleted after any button press
    PINNED, // Pinned message
}
