package com.khabaznia.bot.meta.mapper

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bot.service.ChatService
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.service.MessageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope

@Slf4j
@Component
class MappingHelper {

    @Autowired
    private MessageService messageService
    @Autowired
    private I18nService i18nService
    @Autowired
    private ChatService chatService

    Integer getMessageId(BaseRequest request) {
        Integer.valueOf(request.messageId ?: messageService.getMessage(request.label).messageId)
    }

    BotCommandScope mapScope(com.khabaznia.bot.meta.object.BotCommandScope scope) {
        def apiScope = scope.type.apiClass.newInstance()
        if (apiScope.hasProperty('userId')) apiScope.setUserId(Long.valueOf(scope.userId))
        if (apiScope.hasProperty('chatId')) apiScope.setChatId(scope.chatId)
        apiScope
    }

    String getLocalizedText(BaseRequest source){
        String chatLang = chatService.getChatLang(source.chatId)
        i18nService.getFilledTemplateWithEmoji(source.text, source.binding, source.emoji, chatLang);
    }
}
