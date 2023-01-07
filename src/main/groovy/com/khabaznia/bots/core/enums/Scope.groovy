package com.khabaznia.bots.core.enums

import org.telegram.telegrambots.meta.api.objects.commands.scope.*

enum Scope {

    DEFAULT('default', BotCommandScopeDefault.class),
    ALL_PRIVATE_CHATS('all_private_chats', BotCommandScopeAllPrivateChats.class),
    ALL_GROUP_CHATS('all_group_chats', BotCommandScopeAllGroupChats.class),
    ALL_CHAT_ADMINISTRATORS('all_chat_administrators', BotCommandScopeAllChatAdministrators.class),
    CHAT('chat', BotCommandScopeChat.class),
    CHAT_ADMINISTRATORS('chat_administrators', BotCommandScopeChatAdministrators.class),
    CHAT_MEMBER('chat_member', BotCommandScopeChatMember.class)

    String beanId
    Class apiClass

    Scope(String beanId, Class apiClass) {
        this.beanId = beanId
        this.apiClass = apiClass
    }
}