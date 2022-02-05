package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.util.SessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

abstract class LoggingStrategy implements Configurable {

    @Autowired
    protected ApplicationContext context
    @Autowired
    protected UpdateService updateService

    List<SendMessage> getRequestForEvent(LogEvent event) {
        def request = event.getRequest() ?: context.getBean('sendMessage').text(event.text)
        [request.text((event.skipMetaInfo ? '' : metaInfo) + "$logEmoji " + request.text).chatId(getChatId(event)) as SendMessage]
    }

    protected static String getMetaInfo() {
        "${SessionUtil.currentChat?.code}:${SessionUtil.currentUser?.role?.toString()} "
    }

    protected String getChatId(LogEvent event) {
        getConfig(event.logChat.chatIdConfig) ?: getConfig(LoggingChat.ADMIN.chatIdConfig)
    }

    abstract String getLogEmoji()
}
