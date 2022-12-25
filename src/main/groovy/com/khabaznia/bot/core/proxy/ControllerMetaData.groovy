package com.khabaznia.bot.core.proxy

import groovy.transform.ToString
import org.telegram.telegrambots.meta.api.methods.ActionType

import java.lang.reflect.Method

@ToString
class ControllerMetaData {

    Object bean
    Map<Integer, String> params
    Boolean hasParameters
    Boolean rawParams
    Method beforeExecuteMethod
    Method executeMethod
    Method afterExecuteMethod
    ActionType actionType
    List<String> roles
    String localizedPath
    String originalPath
    String previousPath
    String controllerPath
    Boolean returnString
    Boolean enableDuplicateRequests
}
