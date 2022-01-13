package com.khabaznia.bot.core.proxy

import com.khabaznia.bot.security.authorization.SecuredBotMethod
import com.khabaznia.bot.service.UpdateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Slf4j
@Component
@Scope(value = 'prototype')
class BotControllerProxy {

    @Autowired
    UpdateService updateService

    ControllerMetaData metaData

    @SecuredBotMethod
    String process(final Update update) {
        log.info 'Executing method -> {} : {}', metaData.bean.class.simpleName, metaData.controllerPath
//        metaData.beforeExecuteMethod.invoke(metaData.bean, metaData, update)
        def result = metaData.hasParameters
                ? metaData.executeMethod.invoke(metaData.bean, updateService.getParametersFromUpdate(update))
                : metaData.executeMethod.invoke(metaData.bean)
//        metaData.afterExecuteMethod.invoke(metaData.bean, metaData.currentPath)
        metaData.returnString ? result as String : null
    }
}
