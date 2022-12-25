package com.khabaznia.bot.configuration

import com.khabaznia.bot.enums.UserRole
import com.khabaznia.bot.meta.mapper.RequestMapper
import com.khabaznia.bot.meta.request.impl.GetMe
import com.khabaznia.bot.meta.response.impl.UserResponse
import com.khabaznia.bot.service.BotCommandService
import com.khabaznia.bot.service.BotRequestService
import com.khabaznia.bot.service.JobService
import com.khabaznia.bot.service.UserService
import com.khabaznia.bot.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bot.core.Constants.SWITCHABLE_CONFIG_KEYS_PREFIX
import static com.khabaznia.bot.util.HTMLParsingUtil.*

@Slf4j
@Component
class OnStartupListener implements Configurable {

    @Autowired
    private BotRequestService requestService
    @Autowired
    private UserService userService
    @Autowired
    private Map<String, String> switchableConfigs
    @Autowired
    private RequestMapper requestMapper
    @Autowired
    private JobService jobService
    @Autowired
    private BotCommandService commandService

    @EventListener
    void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug 'Context refreshed'
        addMethods()
        createBotUser()
        configs()
        restoreJobs()
        setAdminAllChatsCommands()
    }

    private void createBotUser() {
        def response = requestService.executeWithResponse(new GetMe()) as UserResponse
        def bot = userService.getUserForCode(response.result.id.toString(), UserRole.BOT)
        log.info 'This bot chat - {}', bot.code
    }

    private static void addMethods() {
        log.debug 'Adding string methods'
        String.metaClass.static.addParams << { Map<String, String> map -> addParamsToCallbackData(delegate, map) }
        String.metaClass.static.addEmoji << { String emoji -> addEmojiToKeyMessage(delegate, emoji) }
        String.metaClass.static.italic << { makeTextItalic(delegate) }
        String.metaClass.static.bold << { makeTextBold(delegate) }
        String.metaClass.static.underline << { makeTextUnderline(delegate) }
        String.metaClass.static.strikethrough << { makeTextStrikethrough(delegate) }
        String.metaClass.static.spoiler << { makeTextAsSpoiler(delegate) }
        String.metaClass.static.linkUrl << { String url -> linkUrl(delegate, url) }
        String.metaClass.static.linkText << { String url -> linkText(delegate, url) }
        String.metaClass.static.spoiler << { makeTextAsSpoiler(delegate) }
        String.metaClass.static.userMentionUrl << { userMention(delegate) }
    }

    private void configs() {
        switchableConfigs.each {
            def fullName = SWITCHABLE_CONFIG_KEYS_PREFIX + 'config.' + it.key
            log.trace '{} : {}', fullName, getConfig(fullName)
        }
    }

    private void restoreJobs() {
        log.trace 'Try to restore jobs'
        jobService.restoreJobs()
    }

    private setAdminAllChatsCommands() {
        commandService.getAdminAllGroupChatsCommands().each {
            requestService.execute(it, false)
        }
    }

}
