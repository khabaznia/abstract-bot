package com.khabaznia.bot.event

import com.khabaznia.bot.enums.LogType
import com.khabaznia.bot.enums.LoggingChat
import com.khabaznia.bot.meta.request.impl.SendMessage

class LogEvent {

    LogType logType = LogType.INFO
    LoggingChat logChat = LoggingChat.LOGGING
    SendMessage request
    String text
    Map<String, String> binding = [:]
    Boolean skipMetaInfo = false
}
