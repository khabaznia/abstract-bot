package com.khabaznia.bot.service

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.enums.ChatType
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User
import com.khabaznia.bot.repository.ChatRepository
import com.khabaznia.bot.repository.ConfigRepository
import com.khabaznia.bot.repository.UserRepository
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.trait.Loggable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE
import static com.khabaznia.bot.core.Constants.ADMIN_CHAT_ID

@Slf4j
@Service
class UserService implements Configurable, Loggable {

    @Autowired
    private UserRepository userRepository
    @Autowired
    private ChatRepository chatRepository
    @Autowired
    private ConfigRepository configRepository

    Chat getChatForCode(String chatCode, String userCode) {
        chatRepository.existsById(chatCode) ?
                processChatForUser(chatCode, userCode) :
                createChat(chatCode, userCode)
    }

    User getUserForCode(String userCode) {
        userRepository.existsById(userCode) ? userRepository.getById(userCode) : createUser(userCode)
    }

    User getUserForCode(String userCode, UserRole userRole) {
        userRepository.existsById(userCode) ? userRepository.getById(userCode) : createUser(userCode, userRole)
    }

    Chat updateChat(Chat chat) {
        chatRepository.save(chat)
    }

    Chat getChatForRole(ChatRole role) {
        chatRepository.findByRole(role).find()
    }

    User getUserForRole(UserRole role) {
        userRepository.findByRole(role).find()
    }

    void setPreviousPath(String path) {
        def lastActionFullPath = SessionUtil.getAttribute(UPDATE_MESSAGE)
        def currentChat = SessionUtil.currentChat
        currentChat.lastAction = path
        if (lastActionFullPath) currentChat.lastActionFullPath = lastActionFullPath
        chatRepository.save(currentChat)
    }

    private Chat processChatForUser(String chatCode, String userCode) {
        def user = userRepository.getById(userCode)
        def chat = chatRepository.getById(chatCode)
        chat?.users?.any { it.code == user.code }
                ? chat
                : addUserToChat(chat, user)
    }

    private Chat createChat(String chatCode, String userCode) {
        def chat = new Chat(code: chatCode, role: ChatRole.NONE, type: getChatType(chatCode), users: [])
        addUserToChat(chat, userRepository.getById(userCode))
    }

    private User createUser(String code) {
        sendLog("New user for code: $code")
        userRepository.save(new User(code: code, role: getUserRole(code), chats: []))
    }

    private Chat addUserToChat(Chat chat, User user) {
//        chat.users = chat.users ?: []
//        user.chats = user.chats ?: []
        chat.users << user
        chatRepository.save(chat)
        user.chats << chat
        userRepository.save(user)
        chat
    }

    private User createUser(String code, UserRole userRole) {
        sendLog("New user for code: $code, userRole: ${userRole.toString()}")
        userRepository.save(new User(code: code, role: userRole, chats: []))
    }

    private UserRole getUserRole(String code) {
        code == getConfig(ADMIN_CHAT_ID) ? UserRole.ADMIN : UserRole.USER
    }

    static ChatType getChatType(String code) {
        code.startsWith('-') ? ChatType.GROUP : ChatType.PRIVATE
    }
}
