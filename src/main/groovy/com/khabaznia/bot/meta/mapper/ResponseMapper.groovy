package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.enums.ChatMemberStatus
import com.khabaznia.bot.meta.response.dto.Media
import com.khabaznia.bot.meta.response.impl.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember

class ResponseMapper {

    static BooleanResponse toResponse(Boolean result) {
        new BooleanResponse(result: result)
    }

    static MessageResponse toResponse(Message result) {
        new MessageResponse(result: new com.khabaznia.bot.meta.response.dto.Message(
                messageId: result.messageId,
                text: result.text,
                media: getMedia(result)))
    }

    static UserResponse toResponse(User user) {
        new UserResponse(result: mapUser(user))
    }

    static ChatResponse toResponse(Chat chat) {
        new ChatResponse(chat: new com.khabaznia.bot.meta.response.dto.Chat(
                id: chat.id,
                type: chat.type,
                inviteLink: chat.inviteLink
        ))
    }

    static ChatMemberResponse toResponse(ChatMember chatMember) {
        new ChatMemberResponse(chatMember: new com.khabaznia.bot.meta.response.dto.ChatMember(
                user: mapUser(chatMember.user),
                status: ChatMemberStatus.valueOf(chatMember.status.toUpperCase())
        ))
    }

    private static Media getMedia(Message result) {
        if (result.hasDocument()) return createMediaDto(result.document)
        if (result.hasPhoto()) return createMediaDto(result.photo[0])
        if (result.hasVideo()) return createMediaDto(result.video)
        if (result.hasAudio()) return createMediaDto(result.audio)
        null
    }

    private static createMediaDto(var obj) {
        new Media(fileId: obj.fileId, fileUniqueId: obj.fileUniqueId)
    }

    private static com.khabaznia.bot.meta.response.dto.User mapUser(User user) {
        new com.khabaznia.bot.meta.response.dto.User(id: user.id)
    }

}
