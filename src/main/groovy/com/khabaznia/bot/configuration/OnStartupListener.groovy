package com.khabaznia.bot.configuration

import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import static com.khabaznia.bot.util.PathParamsUtil.addParamsToCallbackData

@Slf4j
@Component
class OnStartupListener implements Configured {

    @EventListener
    void onApplicationEvent(final ContextRefreshedEvent event) {
        log.debug 'Context refreshed'
        addMethods()
    }

    private static void addMethods() {
        log.debug 'Adding methods'
        String.metaClass.static.addParams << { Map<String, String> map -> addParamsToCallbackData(delegate, map) }
    }
}
