package com.khabaznia.bot.core.interceptor


import com.khabaznia.bot.security.authentication.filter.MultiReadHttpServletRequest
import com.khabaznia.bot.service.ChatService
import com.khabaznia.bot.service.I18nService
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.telegram.telegrambots.meta.api.objects.Update

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.khabaznia.bot.core.Constants.DEFAULT_LOCALE
import static com.khabaznia.bot.security.authentication.filter.AbstractBotFilter.getUpdate
import static com.khabaznia.bot.service.UpdateService.getApiChatFromUpdate
import static com.khabaznia.bot.util.SessionUtil.currentChat

@Slf4j
@Component
class UpdateChatDataInterceptor implements HandlerInterceptor, Configurable {

    @Autowired
    private ChatService chatService
    @Autowired
    private I18nService i18nService

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            def wrappedRequest = new MultiReadHttpServletRequest((HttpServletRequest) request)
            def update = getUpdate(wrappedRequest)
            def apiChat = getApiChatFromUpdate(update)
            log.debug 'Try to update chat data'
            chatService.updateChatTitle(currentChat, apiChat.title)
            if (!currentChat.lang)
                i18nService.changeLocale(getConfig(DEFAULT_LOCALE))
        } catch (Exception ex) {
            log.error('[Minor] Cant update chat info: ' + ex.message)
        }
        true
    }

    private void migrateChatId(Update update) {
        def newChatId = update?.message?.migrateToChatId
        if (newChatId) {
            log.debug 'Migrating chat to id {}', newChatId
            currentChat.code = newChatId
            chatService.updateChat(currentChat)
        }
    }


}
