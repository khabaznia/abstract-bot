package com.khabaznia.bots.core.routing.proxy

import com.khabaznia.bots.core.enums.Role
import com.khabaznia.bots.core.routing.annotation.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ActionType

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.util.stream.Collectors
import java.util.stream.Stream

import static com.khabaznia.bots.core.routing.Constants.*
import static com.khabaznia.bots.core.service.UpdateService.ONE_VARIANT_CONTROLLER_GROUP

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

    private static ControllerMetaData createControllerData(Object bean, Method method, String localizedPath) {
        def controllerMetaData = new ControllerMetaData()
        controllerMetaData.bean = bean
        controllerMetaData.executeMethod = method
        controllerMetaData.returnString = method.returnType == String.class
        controllerMetaData.beforeExecuteMethod = getMethod(bean, BEFORE_METHOD)
        controllerMetaData.afterExecuteMethod = getMethod(bean, AFTER_METHOD)
        controllerMetaData.roles = getRoles(method)
        controllerMetaData.previousPath = getPreviousPath(method)
        controllerMetaData.localizedPath = localizedPath
        controllerMetaData.originalPath = getOriginalPath(method)
        controllerMetaData.controllerPath = getPreviousPath(method) + PREVIOUS_PATH_DELIMITER + localizedPath + roleSuffix(localizedPath, method)
        controllerMetaData.hasParameters = method.parameterCount > 0
        controllerMetaData.rawParams = getRawParams(method)
        controllerMetaData.actionType = getActionType(method)
        controllerMetaData.params = getParams(method)
        controllerMetaData.enableDuplicateRequests = getEnableDuplicateRequests(method)
        controllerMetaData.inputParameterName = getInputParameterName(method)
        controllerMetaData.isMediaInput = getIsMediaInputParam(method)
        controllerMetaData
    }

    private static String getPathFromController(bean) {
        bean.getClass().getAnnotation(BotController.class).path()
    }

    private static List<String> getPathsFromMethod(Method method) {
        def pathFromMethod = getOriginalPath(method)
        hasLocalizedPath(method)
                ? availableLocales.collect { getLocalizedPath(pathFromMethod, it) }.unique() << pathFromMethod
                : Stream.of(pathFromMethod).collect(Collectors.toList())
    }

    private static String getOriginalPath(Method method) {
        method.getAnnotation(BotRequest.class).path()
    }

    private static List<String> getAvailableLocales() {
        environment.getProperty(AVAILABLE_LOCALES).tokenize(CONFIGS_DELIMITER)
    }

    private static String getLocalizedPath(String path, String localeKey) {
        context.getMessage(path, null, new Locale(localeKey))
    }

    private static Method getMethod(bean, String methodName) {
        Arrays.stream(bean.class.methods).find { it?.name == methodName } as Method
    }

    private static String getPreviousPath(Method method) {
        method.getAnnotation(BotRequest.class).after()
    }

    private static Boolean getEnableDuplicateRequests(Method method) {
        method.getAnnotation(BotRequest.class).enableDuplicateRequests()
    }

    private static List<String> getRoles(Method method) {
        ((method.getAnnotation(Secured.class)?.roles() ?: defaultRoles)*.toString()) << Role.BOT.toString()
    }

    private static Role[] getDefaultRoles() {
        Secured.class.getDeclaredMethod('roles').getDefaultValue() as Role[]
    }

    private static Boolean hasLocalizedPath(Method method) {
        Boolean.valueOf(method.isAnnotationPresent(Localized.class))
    }

    private static ActionType getActionType(Method method) {
        method.isAnnotationPresent(Action.class)
                ? method.getAnnotation(Action.class).skip() ? null : method.getAnnotation(Action.class)?.actionType()
                : Action.class.getDeclaredMethod('actionType').getDefaultValue() as ActionType
    }

    private static Map<Integer, String> getParams(Method method) {
        int pos = 0
        new DefaultParameterNameDiscoverer().getParameterNames(method).collectEntries {
            [(pos++): it]
        }
    }

    private static Boolean getRawParams(Method method) {
        method.getAnnotation(BotRequest.class).rawParams()
    }

    private static String roleSuffix(String path, Method method) {
        ONE_VARIANT_CONTROLLER_GROUP*.defaultController.contains(path) && hasSpecificRole(method)
                ? SPECIFIC_ROLE_DELIMITER + getSpecificRole(method)
                : ''
    }

    private static boolean hasSpecificRole(Method method) {
        def roles = getRoles(method)
        def allDefaultRoles = (defaultRoles*.toString()) << Role.BOT.toString()
        !allDefaultRoles.containsAll(roles)
                && roles.size() == 2
                && roles.contains(Role.BOT.toString())
    }

    private static String getSpecificRole(Method method) {
        def roles = getRoles(method) - Role.BOT.toString()
        roles.get(0)
    }

    private static String getInputParameterName(Method method) {
        Integer position = 0
        for (Annotation[] row : method.getParameterAnnotations()) {
            for (Annotation ann : row)
                if (Input.class.isInstance(ann))
                    return getParams(method)[position]
            position++
        }
        null
    }

    private static boolean getIsMediaInputParam(Method method) {
        Integer position = 0
        for (Annotation[] row : method.getParameterAnnotations()) {
            for (Annotation ann : row)
                if (Input.class.isInstance(ann))
                    return ((Input) ann).media()
            position++
        }
        false
    }
}
