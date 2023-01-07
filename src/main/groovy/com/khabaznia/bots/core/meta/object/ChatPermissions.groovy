package com.khabaznia.bots.core.meta.object

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component(value = 'chatPermissions')
@Scope(value = 'prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor
@ToString
class ChatPermissions {

    boolean canSendMessages = true
    boolean canSendMediaMessages = true
    boolean canSendPolls = false
    boolean canSendOtherMessages = false
    boolean canAddWebPagePreviews = false
    boolean canChangeInfo = false
    boolean canInviteUsers = false
    boolean canPinMessages = false
}
