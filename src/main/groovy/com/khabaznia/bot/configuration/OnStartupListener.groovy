package com.khabaznia.bot.configuration

import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.meta.request.impl.GetMe
import com.khabaznia.bot.meta.response.impl.UserResponse
import com.khabaznia.bot.service.ApiMethodService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bot.util.MessageTextsModifyingUtil.addParamsToCallbackData
import static com.khabaznia.bot.util.MessageTextsModifyingUtil.addEmojiToKeyMessage

@Slf4j
@Component
class OnStartupListener implements Configured {

    @Autowired
    private ApiMethodService apiMethodService
    @Autowired
    private UserService userService

    @EventListener
    void onApplicationEvent(final ContextRefreshedEvent event) {
        log.debug 'Context refreshed'
        addMethods()
        createBotUser()
    }

    private void createBotUser() {
        def response = apiMethodService.execute(new GetMe()) as UserResponse
        def bot = userService.getUserForCode(response.result.id.toString(), UserRole.BOT)
        log.info 'This bot chat - {}', bot.code
    }

    private static void addMethods() {
        log.debug 'Adding methods'
        String.metaClass.static.addParams << { Map<String, String> map -> addParamsToCallbackData(delegate, map) }
        String.metaClass.static.addEmoji << { String emoji -> addEmojiToKeyMessage(delegate, emoji) }
    }
}
