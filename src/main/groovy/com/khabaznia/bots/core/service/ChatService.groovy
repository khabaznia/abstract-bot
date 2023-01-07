package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.enums.UserStatusInGroupChat
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton
import com.khabaznia.bots.core.meta.response.impl.ChatResponse
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.model.Permissions
import com.khabaznia.bots.core.repository.ChatRepository
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator

import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat

@Slf4j
@Service
class ChatService implements BaseRequests {

    @Autowired
    private ChatRepository chatRepository
    @Autowired
    private BotRequestService apiMethodService

    Chat getChatByCode(String code) {
        chatRepository.getByCode(code)
    }

    boolean existsChatByCode(String code) {
        chatRepository.existsByCode(code)
    }

    void updateChatTitle(Chat chat, String chatTitle) {
        chat.title = chatTitle
        chatRepository.save(chat)
    }

    void updateChat(Chat chat) {
        chatRepository.save(chat)
    }

    void deleteChat(String chatId) {
        if (chatId && chatRepository.existsById(chatId))
            chatRepository.deleteById(chatId)
    }

    String getChatLang(String code) {
        existsChatByCode(code)
                ? getChatByCode(code).lang
                : null
    }

    Chat updateChatRole(Chat chat, ChatRole chatRole) {
        chat.role = chatRole
        chatRepository.save(chat)
    }

    InlineButton getChatInviteLinkButton(String chatCode, String text = 'button.go.to.chat') {
        def inviteLink = !chatCode ? null : getInviteLinkForChat(chatCode)
        !inviteLink ? null : inlineButton.url(inviteLink).text(text)
    }

    String setChatParam(String key, String value = UUID.randomUUID().toString(),
                        String chatCode = currentChat.code) {
        def chat = chatRepository.getByCode(chatCode)
        if (chat) {
            chat.additionalParams.get(key)
            chat.additionalParams.put(key, value)
            chatRepository.save(chat)
            return value
        }
        null
    }

    String getChatParam(String key, String chatCode = currentChat.code) {
        def chat = chatRepository.getByCode(chatCode)
        !chat ? null : chat.additionalParams.get(key)
    }

    String removeChatParam(String key, String chatCode = currentChat.code) {
        def chat = chatRepository.getByCode(chatCode)
        !chat ? null : chat.additionalParams.remove(key)
    }

    static boolean hasAllPermissions(Chat chat) {
        def perm = chat.permissions
        return perm && perm.canPinMessages && perm.canInviteUsers && perm.canDeleteMessages && perm.canRestrictUsers
    }

    void updateChatPermissions(Chat chat, ChatMemberUpdated chatMemberUpdated) {
        def newChatMember = chatMemberUpdated.newChatMember as ChatMemberAdministrator
        chat.permissions = populatePermissions(newChatMember)
        chatRepository.save(chat)
    }

    private String getInviteLinkForChat(String chatCode) {
        def chatResponse = apiMethodService
                .executeWithResponse(getChat.chatId(chatCode)) as ChatResponse
        chatResponse.chat.inviteLink
    }

    private static Permissions populatePermissions(ChatMemberAdministrator newChatMember) {
        new Permissions(canDeleteMessages: newChatMember.canDeleteMessages,
                canInviteUsers: newChatMember.canInviteUsers,
                canRestrictUsers: newChatMember.canRestrictMembers,
                canPinMessages: newChatMember.canPinMessages)
    }

    static boolean isBotStillMember(ChatMemberUpdated chatMemberUpdated) {
        chatMemberUpdated.newChatMember.status.equalsIgnoreCase(UserStatusInGroupChat.MEMBER.name())
    }

    static boolean isBotPromotedToAdministrator(ChatMemberUpdated chatMemberUpdated) {
        !isBotStillMember(chatMemberUpdated) && chatMemberUpdated.newChatMember.status.equalsIgnoreCase(UserStatusInGroupChat.ADMINISTRATOR.name()) && chatMemberUpdated.oldChatMember.status.equalsIgnoreCase(UserStatusInGroupChat.MEMBER.name())
    }
}
