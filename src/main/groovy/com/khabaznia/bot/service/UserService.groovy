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
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class UserService implements Configurable {

    public static final String ADMIN_CHAT_ID = 'bot.admin.chat.id'
    public static final String LOGGING_CHAT_ID = 'bot.logging.chat.id'

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
        User user = new User()
        user.code = code
        user.role = getUserRole(code)

        userRepository.save(user)
    }

    UserRole getUserRole(String code) {
        code == getConfig(ADMIN_CHAT_ID) ? UserRole.ADMIN : UserRole.USER
    }

    ChatRole getChatRole(String code) {
        code == getConfig(LOGGING_CHAT_ID) ? ChatRole.LOGGING_CHAT : ChatRole.NONE
    }

    static ChatType getChatType(String code) {
        code.startsWith('-') ? ChatType.GROUP : ChatType.PRIVATE
    }
}
