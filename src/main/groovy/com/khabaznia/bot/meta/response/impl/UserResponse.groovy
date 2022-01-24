package com.khabaznia.bot.meta.response.impl

import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.dto.User
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class UserResponse extends BaseResponse {

    User result
}
