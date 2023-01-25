package com.khabaznia.bots.core.flow.strategy.impl

import com.khabaznia.bots.core.flow.strategy.FieldSelectionStrategy
import org.springframework.stereotype.Component

@Component('defaultFieldSelectionStrategy')
class DefaultFieldSelectionStrategy extends FieldSelectionStrategy<Object, Object> {
}
