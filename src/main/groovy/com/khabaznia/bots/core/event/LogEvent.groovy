package com.khabaznia.bots.core.event

import com.khabaznia.bots.core.enums.LogType
import com.khabaznia.bots.core.enums.LoggingChat
import com.khabaznia.bots.core.meta.request.impl.SendMessage

class LogEvent {

    LogType logType = LogType.INFO
    LoggingChat logChat = LoggingChat.LOGGING
    SendMessage request
    String text
    Map<String, String> binding = [:]
    Boolean skipMetaInfo = false
}
