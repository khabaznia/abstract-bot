package com.khabaznia.bot.strategy

import com.khabaznia.bot.enums.ButtonType
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.model.Button
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Slf4j
@Component
class ButtonProcessingStrategyContainer {

    @Autowired
    ApplicationContext context

    private Map<String, String> buttonTypeStrategyMap

    @PostConstruct
    initStrategyMap() {
        buttonTypeStrategyMap = [:]
        buttonTypeStrategyMap.(ButtonType.SIMPLE) = 'noActionButtonProcessingStrategy'
        buttonTypeStrategyMap.(ButtonType.ONE_TIME) = 'oneTimeButtonProcessingStrategy'
        buttonTypeStrategyMap.(ButtonType.SWITCH) = 'switchButtonProcessingStrategy'
        log.trace 'Strategies container: {}', buttonTypeStrategyMap
    }

    ButtonProcessingStrategy getStrategyForButton(Button button) {
        def strategyName = buttonTypeStrategyMap.get(button.type.toString())
        log.debug "Processing button with strategy -> {}", strategyName
        context.getBean(strategyName) as ButtonProcessingStrategy
    }
}
