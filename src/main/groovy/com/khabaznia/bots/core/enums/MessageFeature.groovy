package com.khabaznia.bots.core.enums

enum MessageFeature {

    PERSIST, // Just save to DB
    DELETE, // Delete with next message
    INLINE_KEYBOARD, // Regular inline keyboard, that can be updated
    REPLY_KEYBOARD, // Current reply keyboard
    ONE_TIME_INLINE_KEYBOARD, // Message with inline keyboard that should be deleted after any button press
    PINNED, // Pinned message
    EDIT, // Edit existing message. Updates in db
    MEDIA,  // For AUDIO, IMAGE, VIDEO, DOCUMENT messages

    final static List<MessageFeature> INLINE_KEYBOARD_MESSAGE_GROUP = [INLINE_KEYBOARD, ONE_TIME_INLINE_KEYBOARD]
}
