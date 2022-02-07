package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.ChatRole
import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@Slf4j
abstract class LoggingStrategy implements Configurable {

    @Autowired
    protected ApplicationContext context
    @Autowired
    protected UpdateService updateService
    @Autowired
    protected UserService userService

    List<SendMessage> getRequestsForEvent(LogEvent event) {
        getChatId(event) ? [getLogMessageRequest(event)] : []
    }

    protected getLogMessageRequest(LogEvent event) {
        def request = event.getRequest() ?: context.getBean('sendMessage').text(event.text)
        request.text((event.skipMetaInfo ? '' : metaInfo) + "$logEmoji " + request.text)
                .chatId(getChatId(event)) as SendMessage
    }

    protected static String getMetaInfo() {
        "${SessionUtil.currentChat?.code}:${SessionUtil.currentUser?.role?.toString()} "
    }

    protected String getChatId(LogEvent event) {
        if (event.logChat == LoggingChat.LOGGING)
            return loggingChat
        if (event.logChat == LoggingChat.ADMIN)
            return adminChat
        null
    }

    protected String getLoggingChat() {
        userService.getChatForRole(ChatRole.LOGGING_CHAT)?.code
    }

    protected String getAdminChat() {
        userService.getUserForRole(UserRole.ADMIN)?.code
    }

    abstract String getLogEmoji()
}
