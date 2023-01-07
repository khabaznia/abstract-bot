package com.khabaznia.bots.core.meta.mapper

import com.khabaznia.bots.core.integration.dto.SendMessageDto
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Slf4j
@Component
class ApiDtoMapper {

    @Autowired
    private ApplicationContext context

    SendMessage toBotRequest(SendMessageDto source) {
        SendMessage target = context.getBean("sendMessage")
        target.setChatId(source.getChatId())
        target.setText(source.getText())
        target.setFeatures(source.getFeatures())
        target.inlineKeyboard(source.getButtons())
        target
    }
}
