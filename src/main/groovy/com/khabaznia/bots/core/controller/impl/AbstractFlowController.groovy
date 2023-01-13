package com.khabaznia.bots.core.controller.impl

import com.khabaznia.bots.core.controller.AbstractBotController

abstract class AbstractFlowController extends AbstractBotController {

    protected static <T> T fillDto(Map<String, String> params, T dto) {
        params.findAll { dto.hasProperty(it.key) }
                .each { dto.setProperty(it.key, (it.value == 'null' ? null : it.value)) }
        return dto
    }

    protected <T> T setParams(Map<String, String> params, T dto, String fieldName) {
        dto."$fieldName"(params.findAll { !dto.hasProperty(it.key) })
    }
}
