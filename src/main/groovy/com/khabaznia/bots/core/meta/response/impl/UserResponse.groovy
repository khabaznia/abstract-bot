package com.khabaznia.bots.core.meta.response.impl

import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.meta.response.dto.User
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class UserResponse extends BaseResponse {

    User result
}
