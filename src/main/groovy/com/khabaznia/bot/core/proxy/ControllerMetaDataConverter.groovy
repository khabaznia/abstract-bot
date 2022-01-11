package com.khabaznia.bot.core.proxy

import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.annotation.Localized
import com.khabaznia.bot.core.annotation.Secured
import com.khabaznia.bot.core.annotation.SendAction
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ActionType

import java.lang.reflect.Method
import java.util.stream.Collectors
import java.util.stream.Stream

import static com.khabaznia.bot.configuration.CustomLocaleResolver.*
import static com.khabaznia.bot.core.Constants.*

@Slf4j
@Component
class ControllerMetaDataConverter {

    private static final String BEFORE_METHOD = "before"
    private static final String AFTER_METHOD = "after"

    static ApplicationContext context
    static Environment environment

    @Autowired
    ControllerMetaDataConverter(ApplicationContext context, Environment environment) {
        ControllerMetaDataConverter.context = context
        ControllerMetaDataConverter.environment = environment
    }

    static List<ControllerMetaData> getControllersData(bean, Method method) {
        getPathsFromMethod(method)
                .collect { hasLocalizedPath(method) ? it : (getPathFromController(bean) + it) }
                .collect { createControllerData(bean, method, it) }
    }

    private static ControllerMetaData createControllerData(Object bean, Method method, String currentPath) {
        def controllerMetaData = new ControllerMetaData()
        controllerMetaData.bean = bean
        controllerMetaData.executeMethod = method
        controllerMetaData.returnString = method.returnType == String.class
        controllerMetaData.beforeExecuteMethod = getMethod(bean, BEFORE_METHOD)
        controllerMetaData.afterExecuteMethod = getMethod(bean, AFTER_METHOD)
        controllerMetaData.roles = getRoles(method)
        controllerMetaData.ordered = getOrdered(method)
        controllerMetaData.previousPath = getPreviousPath(method)
        controllerMetaData.currentPath = currentPath
        controllerMetaData.controllerPath = getPreviousPath(method) + PREVIOUS_PATH_DELIMITER + currentPath
        controllerMetaData.hasParameters = method.parameterCount > 0
        controllerMetaData.actionType = getActionType(method)
        log.trace "Get meta data for controller. {} : {}", controllerMetaData.bean.class.simpleName, controllerMetaData.controllerPath
        controllerMetaData
    }

    private static String getPathFromController(bean) {
        bean.getClass().getAnnotation(BotController.class).path()
    }

    private static List<String> getPathsFromMethod(Method method) {
        def pathFromMethod = method.getAnnotation(BotRequest.class).path()
        hasLocalizedPath(method)
                ? availableLocales.collect { getLocalizedPath(pathFromMethod, it) }.unique()
                : Stream.of(pathFromMethod).collect(Collectors.toList())
    }

    private static String[] getAvailableLocales() {
        environment.getProperty(AVAILABLE_LOCALES).split(CONFIGS_DELIMITER)
    }

    private static String getLocalizedPath(String path, String localeKey) {
        context.getMessage(path, null, new Locale(localeKey))
    }

    private static Method getMethod(bean, String methodName) {
        Arrays.stream(bean.class.methods).find { it?.name == methodName } as Method
    }

    private static Boolean getOrdered(Method method) {
        Boolean.valueOf(method.getAnnotation(BotRequest.class).ordered())
    }

    private static String getPreviousPath(Method method) {
        method.getAnnotation(BotRequest.class).after()
    }

    private static List<String> getRoles(Method method) {
        method.getAnnotation(Secured.class)?.roles()*.toString()
    }


    private static Boolean hasLocalizedPath(Method method) {
        Boolean.valueOf(method.isAnnotationPresent(Localized.class))
    }

    private static ActionType getActionType(Method method) {
        method.isAnnotationPresent(SendAction.class)
                ? method.getAnnotation(SendAction.class)?.actionType()
                : ActionType.TYPING
    }
}
