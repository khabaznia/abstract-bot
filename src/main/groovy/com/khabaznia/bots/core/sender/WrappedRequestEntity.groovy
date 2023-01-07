package com.khabaznia.bots.core.sender

import com.khabaznia.bots.core.meta.request.BaseRequest
import groovy.transform.ToString

@ToString
class WrappedRequestEntity {

    BaseRequest request
    Integer countOfRetries
    Object botApiMethod
}
