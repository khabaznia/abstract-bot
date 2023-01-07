package com.khabaznia.bots.common.controller.common

import com.khabaznia.bots.core.controller.AbstractBotController
import com.khabaznia.bots.core.enums.Role
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.routing.annotation.Action
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Secured
import com.khabaznia.bots.core.service.ChatService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.COMMON.DEFAULT
import static com.khabaznia.bots.common.Constants.GROUP_CHATS_ACTIONS_CONTROLLER.*
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentUser

@Slf4j
@Component
@BotController
class GroupChatsActionsController extends AbstractBotController {

    @Autowired
    private ChatService chatService

    @Action(skip = true)
    @BotRequest(path = BOT_CHAT_MEMBER_STATUS_UPDATED, enableDuplicateRequests = true)
    String botStatusUpdated() {
        def chatMemberUpdated = update.getMyChatMember()
        if (chatMemberUpdated) {
            log.debug "Current bot is $chatMemberUpdated.newChatMember.status " +
                    "in chat $chatMemberUpdated.chat.id " +
                    "by $chatMemberUpdated.from.id "
        }
        DEFAULT
    }

    @BotRequest(path = USER_CHAT_MEMBER_STATUS_UPDATED, enableDuplicateRequests = true)
    String userStatusUpdated() {
        def chatMemberUpdated = update.getChatMember()
        log.debug "User $chatMemberUpdated.newChatMember.user.id was $chatMemberUpdated.newChatMember.status " +
                "in chat $chatMemberUpdated.chat.id" +
                "by $chatMemberUpdated.from.id "
        DEFAULT
    }

    @Action(skip = true)
    @BotRequest(path = PROCESS_JOIN_REQUEST, enableDuplicateRequests = true)
    String chatJoinRequest() {
        def chatJoinRequest = update.getChatJoinRequest()
        log.debug "Join request from user $chatJoinRequest.user.id to chat $chatJoinRequest.chat.id"
        DEFAULT
    }

    @BotRequest(path = PROCESS_GROUP_CHAT_CREATED, enableDuplicateRequests = true)
    String processGroupChatCreated() {
        if (currentUser.role == UserRole.ADMIN)
            PROCESS_SERVICE_MESSAGE
        DEFAULT
    }

    @Action(skip = true)
    @BotRequest(path = PROCESS_NEW_CHAT_MEMBERS, enableDuplicateRequests = true)
    String processNewChatMember() {
        if (isAddedByAdmin())
            log.debug 'User added was created by admin'
        DEFAULT
    }

    @Action(skip = true)
    @BotRequest(path = PROCESS_USER_LEFT_CHAT, enableDuplicateRequests = true)
    String processUserLeftChat() {
        if (isAdminLeftChat())
            log.debug 'Admin left chat'
        DEFAULT
    }

    @Action(skip = true)
    @BotRequest(path = PROCESS_SERVICE_MESSAGE, enableDuplicateRequests = true)
    processChatServiceMessages() {
        if (currentChat?.permissions?.canDeleteMessages) {
            log.trace 'Service message {} will be deleted', update?.message?.messageId
            deleteMessage.messageId(update.message.messageId)
        }
    }

    @Secured(roles = Role.ADMIN)
    @BotRequest(path = PROCESS_MIGRATE_TO_CHAT_ID, enableDuplicateRequests = true)
    processMigrateToChatId() {
        sendMessage.text('text.chat.was.migrated')
                .delete()
        chatService.deleteChat(update?.message?.chat?.id?.toString())
    }

    private boolean isAddedByAdmin() {
        userService.getUserRole(update.message.from.id.toString()) == UserRole.ADMIN
    }

    private boolean isAdminAddedToChat() {
        userService.getUserRole(update.message.newChatMembers.first().id.toString()) == UserRole.ADMIN
    }

    private boolean isAdminLeftChat() {
        adminChatId == update?.message?.leftChatMember?.id?.toString()
    }
}
