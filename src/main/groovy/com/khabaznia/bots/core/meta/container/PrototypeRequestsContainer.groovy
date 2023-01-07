package com.khabaznia.bots.core.meta.container

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope('prototype')
@Component(value = 'prototypeRequestsContainer')
class PrototypeRequestsContainer extends RequestsContainer {
}
