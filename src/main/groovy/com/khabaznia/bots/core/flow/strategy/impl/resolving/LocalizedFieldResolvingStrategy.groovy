package com.khabaznia.bots.core.flow.strategy.impl.resolving

import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.trait.Configurable
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.routing.Constants.DEFAULT_LOCALE
import static com.khabaznia.bots.core.util.BotSession.getCurrentChat
import static java.lang.System.lineSeparator

@Component('localizedFieldResolvingStrategy')
class LocalizedFieldResolvingStrategy extends FieldResolvingStrategy implements Configurable {

    @Override
    String getValue(Class entityClass, Long entityId, String fieldName) {
        def allMap = getPersistedValue(entityClass, entityId, fieldName)
        allMap[currentChat.lang] ?: allMap[getConfig(DEFAULT_LOCALE)] ?: allMap.find().value
    }

    @Override
    String getStringView(Object entity, String fieldName) {
        def value = entity."$fieldName"
        if (!value) return null
        lineSeparator().concat((value as Map<String, String>)
                ?.collectEntries { [(Constants.LANG_CONTROLLER.LANG_EMOJI.get(it.key)): it.value] }
                .collect { "$it.key  $it.value" }
                .join(lineSeparator()))
    }
}
