package com.khabaznia.bot.core.handler

import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.core.proxy.BotControllerProxy
import com.khabaznia.bot.core.proxy.ControllerMetaData
import com.khabaznia.bot.core.proxy.ControllerMetaDataConverter
import groovy.util.logging.Slf4j
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.stereotype.Component

@Slf4j
@Component
class BotControllerRegistrar implements BeanPostProcessor, Ordered {

    @Autowired
    protected BotControllerContainer container
    @Autowired
    protected ApplicationContext applicationContext
    @Autowired
    protected ControllerMetaDataConverter converter

    @Override
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        bean
    }

    @Override
    int getOrder() {
        LOWEST_PRECEDENCE
    }

    @Override
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        def beanClass = bean.class
        if (beanClass?.isAnnotationPresent(BotController.class)) {
            beanClass.methods
                    .findAll { it.isAnnotationPresent(BotRequest.class) }
                    .collect { converter.getControllersData bean, it }
                    .flatten()
                    .collect { wrapControllerMetaData it }
                    .each { container.addController it }
        }
        bean
    }

    private BotControllerProxy wrapControllerMetaData(ControllerMetaData metaData) {
        def wrapper = applicationContext.getBean(BotControllerProxy.class)
        wrapper.metaData = metaData
        wrapper
    }
}
