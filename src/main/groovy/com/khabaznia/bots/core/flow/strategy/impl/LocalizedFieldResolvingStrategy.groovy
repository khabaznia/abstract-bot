package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.trait.Configurable
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.routing.Constants.DEFAULT_LOCALE
import static com.khabaznia.bots.core.util.SessionUtil.getCurrentChat

@Component('localizedFieldResolvingStrategyMap')
class LocalizedFieldResolvingStrategy extends FieldResolvingStrategy implements Configurable {

    @Override
    String getValue(Class entityClass, Long entityId, String fieldName) {
        def allMap = getPersistedValue(entityClass, entityId, fieldName)
        allMap[currentChat.lang] ?: allMap[getConfig(DEFAULT_LOCALE)] ?: allMap.find().value
    }
}
