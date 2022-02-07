package com.khabaznia.bot.sender

import com.khabaznia.bot.meta.request.BaseRequest
import groovy.transform.ToString

@ToString
class WrappedRequestEntity {

    BaseRequest request
    Integer countOfRetries
    Object botApiMethod
}
