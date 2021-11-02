package com.khabaznia.bot.core.proxy

import groovy.transform.ToString
import org.telegram.telegrambots.meta.api.methods.ActionType

import java.lang.reflect.Method

@ToString
class ControllerMetaData {

    Object bean
    Boolean hasParameters
    Boolean hasRedirect
    Method beforeExecuteMethod
    Method executeMethod
    Method afterExecuteMethod
    ActionType actionType
    Boolean ordered
    List<String> roles
    String currentPath
    String previousPath
    String controllerPath
    Method keyboard
    String returnString
}
