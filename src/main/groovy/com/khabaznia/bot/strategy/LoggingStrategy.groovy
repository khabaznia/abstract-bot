package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.service.ChatService
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.BaseRequests
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired


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
        request.text("$logEmoji " + request.text)
                .chatId(getChat(event)?.code) as SendMessage
    }

    private SendMessage convertToRequest(LogEvent event) {
        sendMessage.text(i18nService.getFilledTemplate(event.text, event.binding, getChat(event)?.lang))
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
