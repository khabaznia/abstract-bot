package com.khabaznia.bot.enums

enum MessageType {

    SKIP, //Do not save in DB
    PERSIST, // Just save to DB
    DELETE, // Delete with next message
    INLINE_KEYBOARD, // Regular inline keyboard, that can be updated
    REPLY_KEYBOARD, // Current reply keyboard
    ONE_TIME_INLINE_KEYBOARD, // Message with inline keyboard that should be deleted after any button press
    PINNED, // Pinned message
    EDIT, // Edit existing message. Updates in db
    EDIT_AND_DELETE // Edit existing message. Updates in db. Deleted with next message

    final static List<MessageType> FORCE_DELETE_MESSAGE_GROUP = [SKIP, DELETE]
    final static List<MessageType> EDIT_MESSAGE_GROUP = [EDIT, EDIT_AND_DELETE]
    final static List<MessageType> DELETE_MESSAGE_GROUP = [DELETE, EDIT_AND_DELETE]
    final static List<MessageType> INLINE_KEYBOARD_MESSAGE_GROUP = [INLINE_KEYBOARD, ONE_TIME_INLINE_KEYBOARD]

    static List<MessageType> getForceDeleteGroup() { FORCE_DELETE_MESSAGE_GROUP }

    static List<MessageType> getEditGroup() { EDIT_MESSAGE_GROUP }
}
