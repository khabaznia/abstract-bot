package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.enums.ChatType
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.model.Subscription
import com.khabaznia.bots.core.model.User
import com.khabaznia.bots.core.repository.ChatRepository
import com.khabaznia.bots.core.repository.ConfigRepository
import com.khabaznia.bots.core.repository.UserRepository
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.trait.Loggable
import com.khabaznia.bots.core.util.BotSession
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.khabaznia.bots.core.controller.Constants.SESSION_ATTRIBUTES.UPDATE_MESSAGE
import static com.khabaznia.bots.core.routing.Constants.ADMIN_CHAT_ID
import static com.khabaznia.bots.core.routing.Constants.DEFAULT_LOCALE
import static com.khabaznia.bots.core.util.LoggingUtil.getUserInfo

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

    User updateUser(org.telegram.telegrambots.meta.api.objects.User apiUser, User botUser = null) {
        def user = botUser ?: getUserForCode(apiUser.id.toString())
        user.username = apiUser.userName
        user.firstName = apiUser.firstName
        user.lastName = apiUser.lastName
        log.trace 'Updating user data: {}', user
        userRepository.save(user)
    }

    Chat updateChat(Chat chat) {
        chatRepository.save(chat)
    }

    User updateUser(User user) {
        userRepository.save(user)
    }

    Chat getChatForRole(ChatRole role) {
        chatRepository.findByRole(role).find()
    }

    User getUserForRole(UserRole role) {
        userRepository.findByRole(role).find()
    }

    List<String> getSubscriptionUserCodes(boolean isGeneral) {
        userRepository.allByGeneralSubscription*.code
    }

    void setPreviousPath(String path) {
        def lastActionFullPath = BotSession.getStringAttribute(UPDATE_MESSAGE)
        def currentChat = BotSession.currentChat
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
        def chat = new Chat(code: chatCode,
                role: ChatRole.NONE,
                type: getChatType(chatCode),
                users: [],
                lang: getLang(chatCode),
                additionalParams: [:])
        addUserToChat(chat, userRepository.getById(userCode))
    }

    private Chat addUserToChat(Chat chat, User user) {
        chat.users << user
        chatRepository.save(chat)
        user.chats << chat
        userRepository.save(user)
        chat
    }

    private User createUser(String code, UserRole userRole = null) {
        def user = userRepository.save(new User(code: code, role: userRole ?: getUserRole(code), chats: [], subscription: createSubscription(userRole, code)))
        sendLogToAdmin('text.new.user.created', [code: getUserInfo(code), userRole: user.role.toString()], true)
        user
    }

    private Subscription createSubscription(UserRole userRole, String code) {
        def excludedRoles = [UserRole.ADMIN, UserRole.BOT]
        var defaultValue = excludedRoles.contains(userRole) || excludedRoles.contains(getUserRole(code))
        new Subscription(general: !defaultValue)
    }

    private String getLang(String chatCode) {
        getChatType(chatCode) == ChatType.PRIVATE ? null : getConfig(DEFAULT_LOCALE)
    }

    UserRole getUserRole(String code) {
        code == getConfig(ADMIN_CHAT_ID) ? UserRole.ADMIN : UserRole.USER
    }

    static ChatType getChatType(String code) {
        code.startsWith('-') ? ChatType.GROUP : ChatType.PRIVATE
    }
}
