package com.khabaznia.bots.core.strategy

import com.khabaznia.bots.core.enums.ChatRole
import com.khabaznia.bots.core.enums.LoggingChat
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.event.LogEvent
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.service.ChatService
import com.khabaznia.bots.core.service.I18nService
import com.khabaznia.bots.core.service.UpdateService
import com.khabaznia.bots.core.service.UserService
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bots.core.util.SessionUtil.currentChat
import static com.khabaznia.bots.core.util.SessionUtil.currentUser

@Slf4j
abstract class LoggingStrategy implements Configurable, BaseRequests {

    @Autowired
    protected UpdateService updateService
    @Autowired
    protected UserService userService
    @Autowired
    protected I18nService i18nService
    @Autowired
    protected ChatService chatService

    List<SendMessage> getRequestsForEvent(LogEvent event) {
        getChat(event) ? [getLogMessageRequest(event)] : []
    }

    protected BaseRequest getLogMessageRequest(LogEvent event) {
        def request = event.getRequest() ?: convertToRequest(event)
        request.text((event.skipMetaInfo ? '' : metaInfo) + "$logEmoji " + request.text)
                .chatId(getChat(event)?.code) as SendMessage
    }

    private SendMessage convertToRequest(LogEvent event) {
        sendMessage.text(i18nService.getFilledTemplate(event.text, event.binding, getChat(event)?.lang))
    }

    protected static String getMetaInfo() {
        "${currentChat?.code}:${currentUser?.role?.toString()?.linkUrl(currentUser?.code?.userMentionUrl())} "
    }

    protected Chat getChat(LogEvent event) {
        if (event.logChat == LoggingChat.LOGGING)
            return loggingChat
        if (event.logChat == LoggingChat.ADMIN)
            return adminChat
        null
    }

    protected Chat getLoggingChat() {
        userService.getChatForRole(ChatRole.LOGGING_CHAT)
    }

    protected Chat getAdminChat() {
        def user = userService.getUserForRole(UserRole.ADMIN)
        user ? chatService.getChatByCode(user?.code) : null
    }

    abstract String getLogEmoji()
}
