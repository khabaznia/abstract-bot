package com.khabaznia.bot.service

import com.khabaznia.bot.enums.ChatRole
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

import static com.khabaznia.bot.core.Constants.ADMIN_CHAT_ID
import static com.khabaznia.bot.core.Constants.LOGGING_CHAT_ID
import static com.khabaznia.bot.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE_ATTR


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
        chatRepository.existsById(chatCode) ? chatRepository.getById(chatCode) : createChat(chatCode, userCode)
    }

    User getUserForCode(String userCode) {
        userRepository.existsById(userCode) ? userRepository.getById(userCode) : createUser(userCode)
    }

    User getUserForCode(String userCode, UserRole userRole) {
        userRepository.existsById(userCode) ? userRepository.getById(userCode) : createUser(userCode, userRole)
    }

    void setPreviousPath(String path) {
        def lastActionFullPath = SessionUtil.getAttribute(UPDATE_MESSAGE_ATTR)
        def currentChat = SessionUtil.currentChat
        currentChat.lastAction = path
        if (lastActionFullPath) currentChat.lastActionFullPath = lastActionFullPath
        chatRepository.save(currentChat)
    }

    private Chat createChat(String chatCode, String userCode) {
        Chat chat = new Chat();
        chat.code = chatCode
        chat.role = getChatRole(chatCode)
        chat.type = getChatType(chatCode)
        def user = userRepository.getById(userCode)
        chat.users = [user]

        def savedChat = chatRepository.save(chat)
        user.setChat(savedChat)
        userRepository.save(user)
        savedChat
    }

    private User createUser(String code) {
        sendLog('New user for code')
        User user = new User()
        user.code = code
        user.role = getUserRole(code)

        userRepository.save(user)
    }

    private User createUser(String code, UserRole userRole) {
        sendLog("New user for code: $code, userRole: ${userRole.toString()}")
        User user = new User()
        user.code = code
        user.role = userRole

        userRepository.save(user)
    }

    private UserRole getUserRole(String code) {
        code == getConfig(ADMIN_CHAT_ID) ? UserRole.ADMIN : UserRole.USER
    }

    private ChatRole getChatRole(String code) {
        code == getConfig(LOGGING_CHAT_ID) ? ChatRole.LOGGING_CHAT : ChatRole.NONE
    }

    static String getChatType(String code) {
        code.startsWith('-') ? 'group' : 'private'
    }
}
