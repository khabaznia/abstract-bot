package com.khabaznia.bot.strategy

import com.khabaznia.bot.event.LogEvent
import com.khabaznia.bot.meta.request.impl.SendMessage
import com.khabaznia.bot.service.UpdateService
import com.khabaznia.bot.trait.Configured
import com.khabaznia.bot.util.SessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

abstract class LoggingStrategy implements Configured {

    @Autowired
    protected ApplicationContext context
    @Autowired
    protected UpdateService updateService

    List<SendMessage> getRequestForEvent(LogEvent event) {
        def request = event.getRequest() ?: context.getBean('sendMessage').key(event.text)
        [request.key(metaInfo + request.key).chatId(getChatId(event)) as SendMessage]
    }

    String getMetaInfo() {
        "${SessionUtil.currentChat.code} : ${SessionUtil.currentUser.role.toString()} $logEmoji "
    }

    protected String getChatId(LogEvent event) {
        getConfig(event.logChat.chatIdConfig)
    }

    abstract String getLogEmoji()
}
