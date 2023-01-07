package com.khabaznia.bots.core.routing.proxy

import com.khabaznia.bots.core.security.authorization.SecuredBotMethod
import com.khabaznia.bots.core.service.UpdateService
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
    String process(Update update) {
        log.info 'Executing method from class {} ----------> {}', metaData.bean.class.simpleName, metaData.controllerPath
        metaData.beforeExecuteMethod.invoke(metaData.bean, update)
        def params = updateService.getParametersFromUpdate(update)
        log.debug 'Params from update: {}. Params in controller: {}', params, metaData.params
        def result = metaData.hasParameters && !metaData.rawParams
                ? callWithBindedParams(params)
                : metaData.rawParams
                    ? metaData.executeMethod.invoke(metaData.bean, params)
                    : metaData.executeMethod.invoke(metaData.bean)
        metaData.afterExecuteMethod.invoke(metaData.bean, metaData.originalPath)
        metaData.returnString && result ? result as String : null
    }

    private Object callWithBindedParams(params) {
        metaData.executeMethod.invoke(metaData.bean, metaData.params
                .collect { params.get(it.value) } as Object[])
    }
}
