package com.khabaznia.bots.core.routing.proxy

import com.khabaznia.bots.core.security.authorization.SecuredBotMethod
import com.khabaznia.bots.core.service.UpdateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

import static com.khabaznia.bots.core.service.UpdateService.getFileId

@Slf4j
@Component
@Scope(value = 'prototype')
class BotControllerProxy {

    @Autowired
    UpdateService updateService

    ControllerMetaData metaData

    @SecuredBotMethod
    String process(Update update, Map<String, String> redirectParams) {
        log.info 'Executing method from class {} ----------> {}', metaData.bean.class.simpleName, metaData.controllerPath
        metaData.beforeExecuteMethod.invoke(metaData.bean, update)
        def params = updateService.getParametersFromUpdate(update)
        log.debug 'redirect -> {}', redirectParams
        if (redirectParams) params.putAll(redirectParams)
        log.debug 'Params from update: {}. Params in controller: {}', params, metaData.params
        def result = metaData.hasParameters && !metaData.rawParams
                ? callWithBoundParams(params, metaData.inputParameterName, update, metaData.isMediaInput)
                : metaData.rawParams
                    ? metaData.executeMethod.invoke(metaData.bean, params)
                    : metaData.executeMethod.invoke(metaData.bean)
        metaData.afterExecuteMethod.invoke(metaData.bean, metaData.originalPath)
        metaData.returnString && result ? result as String : null
    }

    private Object callWithBoundParams(Map<String, String> params, String inputParamName,
                                       Update update, boolean isMediaInput) {
        metaData.executeMethod.invoke(metaData.bean, metaData.params*.value.collect {
            populateParamValue(inputParamName, it, isMediaInput, update, params)
        } as Object[])
    }

    private String populateParamValue(inputParamName, paramName, isMediaInput,
                                      update, params) {
        (inputParamName && paramName == inputParamName)
                ? isMediaInput ? getFileId(update) : updateService.getMappedMessageText(update)
                : params.get(paramName)
    }
}
